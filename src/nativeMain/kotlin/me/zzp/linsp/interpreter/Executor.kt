package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Expression.Atom
import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Expression.Atom.Companion.t
import me.zzp.linsp.interpreter.Expression.Cons
import me.zzp.linsp.interpreter.Expression.Cons.Lambda

/**
 * The Executor object is responsible for evaluating expressions
 * in the context of the provided bindings.
 */
object Executor {

    /**
     * Evaluates the given [expression] within the provided [bindings] context and returns the result.
     */
    fun eval(expression: Expression, bindings: Bindings = Bindings.GLOBAL): Expression =
        when (expression) {
            is Atom -> eval(expression, bindings) // Read Variable
            is Cons -> eval(expression, bindings) // Call Function
        }

    /**
     * Evaluates an [atom] expression by retrieving the value of a variable
     * with the same name from the [bindings].
     */
    private fun eval(atom: Atom, bindings: Bindings): Expression = bindings[atom.name]

    /**
     * Evaluates a [Cons] expression, which represents a function call or a special form.
     */
    private fun eval(cons: Cons, bindings: Bindings): Expression =
        when (val car = cons.car) {
            is Lambda -> eval(car, cons.cdr, bindings)
            is Atom -> eval(car.name, cons.cdr, bindings)
            is Cons -> eval(Cons(eval(car, bindings), cons.cdr), bindings)
        }

    /**
     * Executes a function call in the form of `((lambda ...) ...)`.
     * This method handles the invocation of a [lambda] expression with
     * the provided actual arguments [expression], within the context
     * of the lambda's closure.
     *
     * Returns the result of evaluating the lambda body within the new [bindings] context.
     */
    private fun eval(lambda: Lambda, expression: Expression, bindings: Bindings): Expression {
        var parameters = lambda.parameters // the formal parameters
        var arguments = expression // actual arguments
        val context = Bindings(lambda.bindings) // closure context

        // Iterate through the formal parameters and actual arguments, binding them together.
        while (parameters is Cons && arguments is Cons) {
            val parameter = parameters.car
            val argument = arguments.car

            if (parameter is Atom) {
                // Ensure each formal parameter is a symbol (Atom),
                // then evaluate the corresponding actual argument.
                context[parameter.name] = eval(argument, bindings)
            } else {
                error("lambda parameter $parameter is not a symbol")
            }

            // Move to the next pair of formal parameter and actual argument.
            parameters = parameters.cdr
            arguments = arguments.cdr
        }
        if (parameters != nil || arguments != nil) {
            error("too few or too many arguments for lambda")
        }

        // Evaluate the body of the lambda within the closure context.
        var result: Expression = nil
        var body = lambda.body
        while (!body.isNil()) {
            if (body is Cons) {
                result = eval(body.car, context)
                body = body.cdr
            } else {
                result = eval(body, context)
                break
            }
        }
        return result // returns the latest value
    }

    /**
     * Evaluates a function call with the specified [name] and [arguments] within the given [bindings],
     * returns the result of the function call.
     */
    private fun eval(name: String, arguments: Expression, bindings: Bindings): Expression =
        when (name) {
            "define" -> define(arguments, bindings)
            "lambda" -> closure(arguments, bindings)
            // Seven Special Forms.
            "quote" -> quote(arguments)
            "atom" -> atom(arguments, bindings)
            "eq" -> eq(arguments, bindings)
            "car" -> car(arguments, bindings)
            "cdr" -> cdr(arguments, bindings)
            "cons" -> cons(arguments, bindings)
            "cond" -> cond(arguments, bindings)
            // For any other function name, it's a user-defined function
            else -> eval(Cons(bindings[name], arguments), bindings)
        }

    // Lambda.

    /**
     * Create a new variable binding or to update an existing one.
     */
    private fun define(arguments: Expression, bindings: Bindings): Expression {
        val (variable, expression) = binary(arguments)
        return if (variable is Atom) {
            eval(expression, bindings).also {
                bindings[variable.name] = it
            }
        } else {
            error("Invalid variable name in define: expected an Atom, found ${variable::class.simpleName}")
        }
    }

    /**
     * Creates a lambda expression, which represents an anonymous function with its associated closure.
     * In Lisp, a lambda expression is a way to define a function inline with its parameters and body.
     *
     * @param function The Cons expression that represents the lambda, where the car is the list of parameters,
     *                  and the cdr is the function body expression.
     * @param bindings The current environment bindings which the lambda may optionally capture.
     * @return A Lambda instance representing the defined function.
     * @throws IllegalStateException if the function does not have both parameters and a body in the correct form.
     */
    private fun closure(function: Expression, bindings: Bindings): Expression =
        if (function is Cons && function.cdr is Cons) {
            Lambda(function.car, function.cdr, bindings)
        } else {
            error("invalid lambda expression: both an argument list and a body are required")
        }

    // Seven Special Forms.

    /**
     * Returns the original expression.
     */
    private fun quote(arguments: Expression): Expression =
        unary(arguments)

    /**
     * Evaluates the first parameter and checks if it is an atom.
     * Returns [t] if the evaluated parameter is an Atom, [nil] otherwise.
     */
    private fun atom(arguments: Expression, bindings: Bindings): Expression =
        if (eval(unary(arguments), bindings) is Atom) {
            t
        } else {
            nil
        }

    /**
     * Evaluates the first two parameters and checks if they are equal.
     * Returns [t] if the evaluated parameters are equal Atoms, [nil] otherwise.
     */
    private fun eq(arguments: Expression, bindings: Bindings): Expression {
        val pair = binary(arguments)
        val left = eval(pair.first, bindings)
        val right = eval(pair.second, bindings)
        return if (left is Atom && right is Atom && left.name == right.name) {
            t
        } else {
            nil
        }
    }

    /**
     * Evaluates the first parameter and returns its car if it is a Cons cell.
     */
    private fun car(arguments: Expression, bindings: Bindings): Expression =
        when (val parameter = eval(unary(arguments), bindings)) {
            nil -> nil
            is Cons -> parameter.car
            else -> error("car requires a cons cell")
        }

    /**
     * Evaluates the first parameter and returns its cdr if it is a Cons cell.
     */
    private fun cdr(arguments: Expression, bindings: Bindings): Expression =
        when (val parameter = eval(unary(arguments), bindings)) {
            nil -> nil
            is Cons -> parameter.cdr
            else -> error("cdr requires a cons cell")
        }

    /**
     * Evaluates the first two parameters and creates a Cons cell from them.
     */
    private fun cons(arguments: Expression, bindings: Bindings): Expression {
        val (left, right) = binary(arguments)
        return Cons(eval(left, bindings), eval(right, bindings))
    }

    /**
     * Evaluates a series of (condition action) pairs and returns the result of the first true condition.
     */
    private fun cond(arguments: Expression, bindings: Bindings): Expression {
        var cons = arguments
        while (true) {
            if (cons is Cons) {
                val (condition, action) = binary(cons.car)
                cons = cons.cdr
                val result = eval(condition, bindings)
                if (!result.isNil()) {
                    return eval(action, bindings)
                }
            } else if (cons.isNil()) {
                break
            } else {
                error("cond requires a series of (condition action) pairs")
            }
        }
        return nil
    }

    // Parameters Parsing.

    /**
     * Ensures that there is only one parameter and retrieves it.
     */
    private fun unary(arguments: Expression): Expression =
        if (arguments is Cons && arguments.cdr.isNil()) {
            arguments.car
        } else {
            error("Unary function requires exactly one argument")
        }

    /**
     * Ensures that there are exactly two parameters and retrieves them as a [Pair]<[Expression], [Expression]>.
     */
    private fun binary(arguments: Expression): Pair<Expression, Expression> =
        if (arguments is Cons && arguments.cdr is Cons && arguments.cdr.cdr.isNil()) {
            arguments.car to arguments.cdr.car
        } else {
            error("Binary function requires exactly two arguments")
        }
}
