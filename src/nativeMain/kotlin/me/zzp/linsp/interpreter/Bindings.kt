package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Expression.Atom.Companion.t

/**
 * Represents a scope of variable bindings within the environment.
 */
data class Bindings(

    /**
     * The enclosing (parent) scope from which this scope inherits bindings.
     */
    private val parent: Bindings? = null,
) {

    /**
     * The local context or scope that holds the variable bindings specific to this environment level.
     */
    private val context = mutableMapOf<String, Expression>()

    /**
     * A convenient constructor for creating a Bindings instance with an initial set of variable bindings.
     */
    constructor(vararg pairs: Pair<String, Expression>, parent: Bindings? = null) : this(parent) {
        context.putAll(pairs)
    }

    /**
     * Retrieves the value of a variable by [name] from the current scope.
     *
     * - If the variable is not found, it attempts to retrieve from the parent scope.
     * - If the variable is not found in any scope, it throws a [NoSuchElementException].
     */
    operator fun get(name: String): Expression =
        context[name] ?: parent?.get(name) ?: throw NoSuchElementException(name)

    /**
     * Sets the [expression] value of a [name] variable in the current scope.
     */
    operator fun set(name: String, expression: Expression) {
        context[name] = expression
    }

    companion object {

        /**
         * The global environment of the Lisp interpreter, containing predefined
         * bindings that are available throughout the execution of the interpreter.
         * The global environment serves as the outermost scope from which all other local scopes inherit.
         */
        val GLOBAL = Bindings(
            "t" to t,
            "nil" to nil,
        )
    }
}
