package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Expression.Atom
import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Expression.Cons

/**
 * The [Formatter] class is responsible for converting Lisp expressions into their string representations.
 * It provides a human-readable format for displaying the structure of expressions, which is essential
 * for debugging and presenting the results of evaluated code.
 */
object Formatter {

    /**
     * Converts the given Lisp [expression] into its [String] representation.
     */
    fun format(expression: Expression): String =
        when (expression) {
            is Atom -> format(expression)
            is Cons -> format(expression)
        }

    /**
     * Formats an [Atom] expression, which represents a basic Lisp element like a symbol or a constant.
     * Returns the name of the Atom as a [String].
     */
    private fun format(atom: Atom): String = atom.name

    /**
     * Formats a [Cons] expression as a parenthesized list.
     */
    private fun format(cell: Cons): String = StringBuilder("(").let { builder ->
        var cons: Cons = cell
        while (true) {
            builder.append(format(cons.car))
            when (val cdr = cons.cdr) {
                is Atom -> {
                    if (cdr != nil) {
                        // Dotted pair notation.
                        builder.append(" . ").append(format(cdr))
                    }
                    break
                }
                is Cons -> {
                    // Continue the list by appending a space and the CDR's CAR.
                    builder.append(" ")
                    cons = cdr
                }
            }
        }
        builder.append(")").toString()
    }
}
