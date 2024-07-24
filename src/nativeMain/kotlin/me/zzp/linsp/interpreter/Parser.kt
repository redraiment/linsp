package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Expression.Atom
import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Expression.Cons
import me.zzp.linsp.interpreter.Token.Identifier
import me.zzp.linsp.interpreter.Token.Literal.CloseBracket
import me.zzp.linsp.interpreter.Token.Literal.OpenBracket
import me.zzp.linsp.interpreter.Token.Literal.Quote

/**
 * The [Parser] class is responsible for converting a sequence of tokens into a parse tree.
 * The parse tree is a hierarchical representation of the source code that adheres to
 * the language's grammar rules.
 */
data class Parser(

    /**
     * An iterator over the stream of tokens produced by the Lexer.
     * This stream serves as the input for the parsing process.
     */
    private val tokens: Iterator<Token>,
) : Iterator<Expression> {

    /**
     * Determines if there are any tokens remaining to be read and parsed.
     */
    override fun hasNext(): Boolean = tokens.hasNext()

    /**
     * Retrieves the next S-expression from the token stream.
     * If a mismatched closing bracket is encountered, an error is thrown.
     */
    override fun next(): Expression =
        readExpression() ?: error("Mismatched closing bracket detected")

    /**
     * Returns next S-expression with the following processing steps:
     *
     * + Expands reader macros:
     *   - [Quote] tokens are transformed into the form `(quote <expression>)`,
     *     allowing for the inclusion the symbols or lists without evaluation.
     * + Reads cons cells to form lists:
     *   - [OpenBracket]: initiates the capture of elements within brackets,
     *     continuing until the matching [CloseBracket] is found.
     *   - [CloseBracket]: indicates the end of a list, returning `null` to signify
     *     no further elements (not representing an actual expression).
     * + Reads identifiers as symbols or variable names:
     *   - [Identifier] tokens represent a sequence of characters without whitespace,
     *     serving as a symbol or variable name.
     */
    private fun readExpression(): Expression? =
        when (val token = tokens.next()) {
            // 1. Expands reader macros:
            Quote -> Cons(Atom.quote, Cons(next()))
            // 2. Reads cons cells:
            OpenBracket -> readListElements() ?: nil
            CloseBracket -> null
            // 3. Reads identifiers:
            is Identifier -> Atom(token.name)
        }


    /**
     * Recursively parses list elements, forming a nested expression
     * until the corresponding [CloseBracket] is detected.
     * - Returns `null` (representing `nil`) if the list is empty.
     * - Otherwise, constructs a `Cell` containing the list elements,
     * encapsulating the parsed structure.
     */
    private fun readListElements(): Expression? = readExpression()?.let { car ->
        Cons(car, readListElements() ?: nil)
    }
}
