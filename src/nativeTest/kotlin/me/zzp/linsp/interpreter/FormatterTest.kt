package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Expression.Atom
import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Formatter.format
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test [Formatter] object.
 */
class FormatterTest {

    @Test
    fun `test atom format`() {
        println(format(nil))
        assertEquals("nil", format(nil))
        println(format(Atom("atom")))
        assertEquals("atom", format(Atom("atom")))
    }

    @Test
    fun `test cons format`() {
        println(format(list(Atom("A"), Atom("B"))))
        assertEquals("(A B)", format(list(Atom("A"), Atom("B"))))
        println(format(list(
            list(Atom("lambda"), list(Atom("name")), list(Atom("display"), Atom("name"))),
            list(Atom("quote"), Atom("joe"))
        )))
        assertEquals("((lambda (name) (display name)) (quote joe))", format(list(
            list(Atom("lambda"), list(Atom("name")), list(Atom("display"), Atom("name"))),
            list(Atom("quote"), Atom("joe"))
        )))
    }
}