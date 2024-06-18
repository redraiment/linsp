package me.zzp.interpreter

/**
 * 词法解析后的标记。
 */
sealed interface Token {

    /**
     * 字面量标记。
     */
    enum class Literal(val token: Char) : Token {

        /**
         * 开括号。
         */
        OpenBracket('('),

        /**
         * 闭括号。
         */
        CloseBracket(')'),

        /**
         * 单引号。
         */
        Quote('\'');
    }

    /**
     * 标识符。
     */
    data class Identifier(val name: String) : Token
}
