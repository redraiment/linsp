package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Bindings.Companion.GLOBAL
import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Expression.Cons

/**
 * The S-expression, or Symbolic Expression, is a foundational data structure in Lisp.
 * It is versatile, serving to represent both code and data within the language.
 * This dual capability is key to Lisp's unique approach to programming.
 */
sealed interface Expression {

    fun isNil(): Boolean = this is Atom && this.name == "nil"

    /**
     * An [Atom] represents the most basic form of S-expressions.
     * It is considered a single, indivisible unit.
     *
     * *Note*: Linsp currently supports only the symbol type for atoms.
     */
    data class Atom(val name: String) : Expression {

        /**
         * Built-in Atoms
         */
        companion object {

            /**
             * Logical True.
             */
            val t = Atom("t")

            /**
             * Logical False & empty list/cons cell.
             */
            val nil = Atom("nil")

            /**
             * quote reader macro.
             */
            val quote = Atom("quote")
        }
    }

    /**
     * The Cons Cell is a fundamental building block in Lisp,
     * essential for creating lists and other complex data structures.
     *
     * - The [car] represents the first element of the cell,
     * often referred to as the "head."
     * - The [cdr] denotes the remainder of the cell,
     * which can be another Cons Cell or `nil` if it is the last element.
     *
     * *Note*: `nil` signifies an empty cell, but `(nil)` is a list
     * containing the symbol `nil`, not an empty cell.
     */
    open class Cons(val car: Expression, val cdr: Expression = nil) : Expression {

        /**
         * Represents a lambda expression, which is a special type of Cons
         * where the first element is the 'lambda' atom. Lambda expressions
         * encapsulate a function's parameters, body, and the environment in
         * which the function is defined, allowing for function closures with
         * access to the surrounding context.
         */
        data class Lambda(

            /**
             * The formal parameters of the lambda function.
             */
            val parameters: Expression,

            /**
             * The body of the lambda function, which is the [Expression]
             * that will be evaluated when the function is called.
             */
            val body: Expression,

            /**
             * The environment in which the lambda is defined, allowing it
             * to capture and access variables from the surrounding scope.
             */
            val bindings: Bindings = GLOBAL,
        ) : Cons(parameters, body)
    }
}

/**
 * Creates a list Expression from a variable number of expressions.
 * This utility function simplifies the creation of a list Expression by accepting
 * a variable number of [Expression] arguments and converting them into a single Expression.
 * Returns a Cons cell representing the list if the input list is not empty,
 * otherwise, it returns the [nil] atom to represent an empty list.
 */
fun list(vararg expressions: Expression): Expression =
    if (expressions.isNotEmpty()) {
        // Construct a right-leaning list starting from the last element.
        expressions.foldRight(nil, ::Cons)
    } else {
        // Return 'nil' for an empty list.
        nil
    }
