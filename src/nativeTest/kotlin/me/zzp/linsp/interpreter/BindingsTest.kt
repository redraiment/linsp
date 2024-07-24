package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Expression.Atom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Test [Bindings] class.
 */
class BindingsTest {

    @Test
    fun `test undefined`() {
        val bindings = Bindings()
        val name = "UNDEFINED"
        try {
            println(bindings[name])
            fail("Never been there")
        } catch (e: NoSuchElementException) {
            assertEquals(name, e.message)
        }
    }

    @Test
    fun `test get from current scope`() {
        val bindings = Bindings()
        val name = "user-name"
        val expected = Atom("Joe")
        bindings[name] = expected
        assertEquals(expected, bindings[name])
    }

    @Test
    fun `test get from parent scope`() {
        val parent = Bindings()
        val name = "user-name"
        val expected = Atom("Joe")
        parent[name] = expected

        val bindings = Bindings(parent)
        assertEquals(expected, bindings[name])
    }
}
