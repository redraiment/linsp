package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Token.Identifier
import me.zzp.linsp.interpreter.Token.Literal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test [Lexer] class.
 */
class LexerTest {

    @Test
    fun `test hasNext`() {
        assertTrue(Lexer("123").hasNext())
        assertFalse(Lexer("").hasNext())
        assertFalse(Lexer("  \n ").hasNext())
    }

    @Test
    fun `test next literal`() {
        for (code in listOf("(", ")", "'")) {
            val token = Lexer(code).next()
            assertTrue(token is Literal)
            assertEquals(code[0], token.value)
        }
    }

    @Test
    fun `test next number`() {
        val token = Lexer("123 456").next()
        assertTrue(token is Identifier)
        assertEquals("123", token.name)
    }

    @Test
    fun `test next variable`() {
        val token = Lexer("*command-line-args* hello world").next()
        assertTrue(token is Identifier)
        assertEquals("*command-line-args*", token.name)
    }
}
