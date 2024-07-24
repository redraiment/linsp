package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Expression.Atom
import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Expression.Cons
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test [Expression] class.
 */
class ExpressionTest {

    @Test
    fun `test is nil`() {
        assertTrue(nil.isNil())
        assertTrue(Atom("nil").isNil())
        assertTrue(list().isNil())
        assertFalse(Atom("atom").isNil())
        assertFalse(list(nil).isNil())
        assertFalse(list(Atom("atom")).isNil())
    }

    @Test
    fun `test empty list`() {
        val nil = list()
        assertNil(nil)
    }

    @Test
    fun `test nil element`() {
        val cons = list(nil)
        assertTrue(cons is Cons)
        assertNil(cons.car)
        assertNil(cons.cdr)
    }

    @Test
    fun `test nil elements`() {
        var cons = list(nil, nil)
        assertTrue(cons is Cons)
        assertNil(cons.car)

        cons = cons.cdr
        assertTrue(cons is Cons)
        assertNil(cons.car)
        assertNil(cons.cdr)
    }

    @Test
    fun `test nested list`() {
        var cons: Expression = list(Atom("1"), list(Atom("2.1"), Atom("2.2")), Atom("3"))
        cons = assertCar("1", cons)
        assertTrue(cons is Cons)

        var children: Expression = cons.car
        children = assertCar("2.1", children)
        assertTrue(children is Cons)
        children = assertCar("2.2", children)
        assertNil(children)

        assertNil(assertCar("3", cons.cdr))
    }

    /**
     * Asserts that the provided expression evaluates to nil,
     * the Lisp representation of the empty value.
     * @param expression The expression to evaluate and assert as nil.
     */
    private fun assertNil(expression: Expression) =
        assertAtom("nil", expression)

    /**
     * Asserts that the provided expression is an Atom with the expected name.
     * This function is used to verify that an expression is a basic Lisp element
     * such as a symbol or a constant.
     * @param expected The expected name of the Atom.
     * @param expression The expression to assert as an Atom.
     */
    private fun assertAtom(expected: String, expression: Expression) {
        assertTrue(expression is Atom)
        assertEquals(expected, expression.name)
    }

    /**
     * Asserts that the car (first element) of a Cons expression is an Atom with the expected name,
     * and returns the cdr (rest of the expression).
     * This function is used to verify the first element of a list-like structure in Lisp and to progress
     * the evaluation to the next element in the list.
     * @param expected The expected name of the car Atom.
     * @param expression The Cons expression to evaluate and assert the car of.
     * @return The cdr of the original expression.
     */
    private fun assertCar(expected: String, expression: Expression): Expression {
        assertTrue(expression is Cons)
        assertAtom(expected, expression.car)
        return expression.cdr
    }
}
