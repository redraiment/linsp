package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Expression.Atom
import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Expression.Atom.Companion.t
import me.zzp.linsp.interpreter.Expression.Cell
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 测试表达式。
 */
class ExpressionTest {

    private val bindings: Bindings
        get() = Bindings().also {
            it["t"] = t
            it["nil"] = nil
        }

    private fun assertExpression(expression: String, expected: Expression) {
        assertEquals(expected, Parser(Lexer(expression)).next().eval(bindings))
    }

    @Test
    fun testnil() {
        assertExpression("nil", nil)
        assertExpression("()", nil)
        assertExpression("'()", nil)
        assertExpression(" ( ) ", nil)
    }

    @Test
    fun testTrue() {
        assertExpression("t", t)
        assertExpression("'t", t)
    }

    @Test
    fun testAtom() {
        assertExpression("(atom nil)", t)
        assertExpression("(atom ())", t)
        assertExpression("(atom (quote nil))", t)
        assertExpression("(atom 'one)", t)
        assertExpression("(atom '(one))", nil)
    }

    @Test
    fun testEq() {
        assertExpression("(eq nil nil)", t)
        assertExpression("(eq nil ())", t)
        assertExpression("(eq () nil)", t)
        assertExpression("(eq () ())", t)
        assertExpression("(eq nil 'atom)", nil)
        assertExpression("(eq 'atom 'atom)", t)
        assertExpression("(eq '(one two) nil)", nil)
        assertExpression("(eq '(one two) '(one two))", nil)
    }

    @Test
    fun testCar() {
        assertExpression("(car nil)", nil)
        assertExpression("(car '(one))", Atom("one"))
        assertExpression("(car '(one two))", Atom("one"))
    }

    @Test
    fun testCdr() {
        assertExpression("(cdr nil)", nil)
        assertExpression("(cdr '(one))", nil)
        assertExpression("(cdr '(one two))", Cell(Atom("two")))
        assertExpression("(cdr '(one two three))", (Cell(Atom("two"), Cell(Atom("three")))))
    }

    @Test
    fun testCons() {
        assertExpression("(cons 'one 'two)", Cell(Atom("one"), Atom("two")))
        assertExpression("(cons 'one (cons 'two nil))", Cell(Atom("one"), Cell(Atom("two"), nil)))
    }

    @Test
    fun testCond() {
        assertExpression("(cond)", nil)
        assertExpression("(cond ('t 'yes))", Atom("yes"))
        assertExpression("""
            (cond
              ((eq 'one 'two) 'no)
              ((eq 'one 'one) 'yes))
        """.trimIndent(), Atom("yes")
        )
    }
}
