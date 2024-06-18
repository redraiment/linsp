package me.zzp.interpreter

import me.zzp.interpreter.Expression.Atom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * 测试Bindings。
 */
class BindingsTest {

    /**
     * 当变量缺失时抛出异常。
     */
    @Test
    fun testMissing() {
        val bindings = Bindings()
        val name = "MISSING"
        try {
            println(bindings[name])
            fail("Never been there")
        } catch (e: NoSuchElementException) {
            assertEquals(name, e.message)
        }
    }

    /**
     * 添加了变量后能成功获取。
     */
    @Test
    fun testGetAfterSet() {
        val bindings = Bindings()
        val name = "name"
        val expected = Atom("Joe")
        bindings[name] = expected
        assertEquals(expected, bindings[name])
    }

    /**
     * 在父级作用域内添加了变量后，也能成功获取。
     */
    @Test
    fun testGetAfterSetParent() {
        val parent = Bindings()
        val bindings = Bindings(parent)
        val name = "name"
        val expected = Atom("Joe")
        parent[name] = expected
        assertEquals(expected, bindings[name])
    }
}
