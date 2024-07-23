package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Token.Identifier
import me.zzp.linsp.interpreter.Token.Literal

/**
 * The Lexer class is responsible for converting a string of source code into a sequence of tokens.
 * It implements the Iterator interface to provide a stream of Token objects.
 */
data class Lexer(

    /**
     * The source code to be tokenized.
     */
    private val code: String
) : Iterator<Token> {

    /**
     * The index pointing to the beginning of the next token to be read.
     */
    private var index = 0

    /**
     * Indicates whether the end of the file (EOF) has been reached.
     */
    private val eof: Boolean get() = index >= code.length

    /**
     * The current character at the pointer's position in the source code.
     */
    private val character: Char get() = code[index]

    /**
     * Determines if there are more tokens available after skipping any leading whitespace.
     */
    override fun hasNext(): Boolean {
        readWhitespaces()
        return !eof
    }

    /**
     * Retrieves the next available token, which can be either a [Literal] or an [Identifier].
     */
    override fun next(): Token = readLiteral() ?: readIdentifier()

    /**
     * Advances the pointer to the next character in the source code.
     */
    private fun readCharacter(): Char = code[index++]

    /**
     * Advances the pointer past any whitespace characters
     * until a non-whitespace character is encountered.
     */
    private fun readWhitespaces() {
        while (!eof && character.isWhitespace()) {
            readCharacter()
        }
    }

    /**
     * Constructs and returns a Literal token if the current character corresponds to a known literal.
     * Advances the pointer to the next character after reading a literal.
     */
    private fun readLiteral(): Literal? = character.toLiteralOrNull()?.also {
        readCharacter()
    }

    /**
     * Constructs and returns an Identifier token from the current position in the source code.
     * The pointer is advanced to the end of the identifier.
     */
    private fun readIdentifier(): Identifier {
        val source = index
        while (!eof && character.isIdentifier()) {
            readCharacter()
        }
        val target = index
        return Identifier(code.substring(source, target))
    }
}
