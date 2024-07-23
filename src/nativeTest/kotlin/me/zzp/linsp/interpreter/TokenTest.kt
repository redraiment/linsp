package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Token.Literal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Test [Token] class。
 */
class TokenTest {

    @Test
    fun `test literals`() {
        assertTrue('('.isLiteral())
        assertTrue(')'.isLiteral())
        assertTrue('\''.isLiteral())
        assertFalse(' '.isLiteral())
        assertFalse('*'.isLiteral())

        assertEquals(Literal.OpenBracket, '('.toLiteralOrNull())
        assertEquals(Literal.CloseBracket, ')'.toLiteralOrNull())
        assertEquals(Literal.Quote, '\''.toLiteralOrNull())
        assertNull(' '.toLiteralOrNull())
    }

    @Test
    fun `test identifiers`() {
        assertTrue('*'.isIdentifier())
        assertTrue('$'.isIdentifier())
        assertTrue('0'.isIdentifier())
        assertTrue('A'.isIdentifier())
        assertFalse(' '.isIdentifier())
        assertFalse('\n'.isIdentifier())
    }
}
