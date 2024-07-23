package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Token.Literal

/**
 * Represents lexical units, commonly referred to as tokens.
 * Tokens are the elementary components of source code, parsed and categorized
 * by the lexical analyzer during the compilation process. They serve as the
 * building blocks for constructing the syntax tree and are essential for
 * further syntactic and semantic analysis.
 */
sealed interface Token {

    /**
     * Represents a fixed, unchanging value as a lexical token.
     * This enumeration defines different types of literal tokens
     * that can appear in the source code.
     */
    enum class Literal(val value: Char) : Token {

        /**
         * Denotes the beginning of a group or list.
         */
        OpenBracket('('),

        /**
         * Signifies the end of a group or list.
         */
        CloseBracket(')'),

        /**
         * Used to indicate the quotation of an expression or literal.
         */
        Quote('\'');
    }

    /**
     * Represents a token that corresponds to a name assigned to entities
     * such as variables, functions, classes, or any other user-defined
     * identifiers within the code.
     *
     * This class encapsulates the properties of an identifier,
     * serving as a fundamental unit in the lexical analysis of source code.
     */
    data class Identifier(val name: String) : Token
}

/**
 * A map of built-in literals, keyed by their character representations.
 * This provides efficient lookup of literal tokens by their corresponding characters.
 */
private val literals = Literal.entries.associateBy { it.value }

/**
 * Converts this character to a corresponding Literal token if one exists.
 * Returns null if the character does not correspond to any literal token.
 */
fun Char.toLiteralOrNull(): Literal? = literals[this]

/**
 * Determines whether this character represents a literal token.
 */
fun Char.isLiteral(): Boolean = this in literals

/**
 * Determines if this character can be part of an identifier.
 * An identifier character must not be whitespace and must not be a literal token.
 */
fun Char.isIdentifier(): Boolean = !this.isWhitespace() && this !in literals
