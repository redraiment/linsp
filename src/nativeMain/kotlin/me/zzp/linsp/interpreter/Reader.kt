package me.zzp.linsp.interpreter

/**
 * The Reader class acts as an iterator for reading and converting
 * source code into a sequence of Expression objects.
 * It encapsulates the logic for tokenizing the source code and
 * parsing it into a hierarchical structure.
 */
data class Reader(

    /**
     * The source code that is to be tokenized and parsed into expressions.
     */
    private val code: String
) : Iterator<Expression> {

    /**
     *  A parser instance that converts the stream of tokens
     *  from the Lexer into a sequence of Expression objects.
     */
    private val parser = Parser(Lexer(code))

    /**
     * Determines if there are more expressions available to be read from the source code.
     */
    override fun hasNext(): Boolean = parser.hasNext()

    /**
     * Retrieves the next available Expression from the source code.
     */
    override fun next(): Expression = parser.next()
}
