package me.zzp.interpreter

import me.zzp.interpreter.Expression.Atom
import me.zzp.interpreter.Expression.Atom.Companion.nil
import me.zzp.interpreter.Expression.Cell
import me.zzp.interpreter.Token.Identifier
import me.zzp.interpreter.Token.Literal.CloseBracket
import me.zzp.interpreter.Token.Literal.OpenBracket
import me.zzp.interpreter.Token.Literal.Quote

/**
 * 语法解析器。
 */
data class Parser(
    /**
     * 词法解析器。
     */
    private val lexer: Lexer
) : Iterator<Expression> {

    /**
     * 是否还有剩余的表达式。
     */
    override fun hasNext(): Boolean =
        lexer.hasNext()

    /**
     * 读取下一个表达式。
     */
    override fun next(): Expression =
        nextExpression() ?: error("Missing open bracket")

    /**
     * 读取下一个表达式：特殊处理空列表和闭括号。
     */
    private fun nextExpression(): Expression? =
        if (hasNext()) {
            when (val token = lexer.next()) {
                OpenBracket -> nextList() ?: nil
                CloseBracket -> null
                Quote -> Cell(Atom.quote, Cell(next()))
                is Identifier -> Atom(token.name)
            }
        } else {
            error("requires expression")
        }

    /**
     * 递归地读取完整的列表中，直到遇到闭括号。
     */
    private fun nextList(): Expression? = nextExpression().let { car ->
        if (car == null) {
            car
        } else {
            Cell(car, nextList() ?: nil)
        }
    }
}
