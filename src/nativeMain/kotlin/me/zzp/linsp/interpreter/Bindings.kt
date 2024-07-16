package me.zzp.linsp.interpreter

/**
 * 变量绑定（作用域）。
 */
data class Bindings(
    /**
     * 上级作用域。
     */
    private val parent: Bindings? = null,
) {
    /**
     * 当前作用域。
     */
    private val context = mutableMapOf<String, Expression>()

    /**
     * 方便的初始化方法。
     */
    constructor(vararg pairs: Pair<String, Expression>, parent: Bindings? = null) : this(parent) {
        context.putAll(pairs)
    }

    /**
     * 先尝试从当前作用域获取。
     * 若获取不到，则从上级作用域获取。
     * 若仍然获取不到，则抛出异常。
     */
    operator fun get(name: String): Expression =
        context[name] ?: parent?.get(name) ?: throw NoSuchElementException(name)

    /**
     * 在当前作用域中添加变量值。
     */
    operator fun set(name: String, expression: Expression) {
        context[name] = expression
    }
}
