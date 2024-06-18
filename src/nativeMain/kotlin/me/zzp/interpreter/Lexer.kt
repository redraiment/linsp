package me.zzp.interpreter

import me.zzp.interpreter.Token.Identifier
import me.zzp.interpreter.Token.Literal

/**
 * 词法解析器。
 */
data class Lexer(
    /**
     * 完整的源代码。
     */
    private val code: String
) : Iterator<Token> {

    companion object {
        val literals = Literal.entries.associateBy { it.token }
    }

    /**
     * 下一个将读取的字符的下标。
     */
    private var index = 0

    /**
     * 是否还有剩余的Token。
     */
    override fun hasNext(): Boolean {
        while (index < code.length && code[index].isWhitespace()) {
            // 跳过空白字符
            index++
        }
        return index < code.length
    }

    /**
     * 获取下一个标记。
     */
    override fun next(): Token {
        val source = index++ // 第一个非空字符
        if (code[source] in literals) { // 单字符字面量标记。
            return literals[code[source]]!!
        }
        while (index < code.length && code[index] !in literals && !code[index].isWhitespace()) {
            index++
        }
        return Identifier(code.substring(source, index)) // 否则，是标识符标记。
    }
}
