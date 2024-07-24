package me.zzp.linsp.interpreter

import me.zzp.linsp.interpreter.Executor.eval
import me.zzp.linsp.interpreter.Expression.Atom
import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Expression.Atom.Companion.t
import me.zzp.linsp.interpreter.Expression.Cons
import me.zzp.linsp.interpreter.Expression.Cons.Lambda
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test [Executor] object.
 */
class ExecutorTest {

    @Test
    fun `test built-in variables`() {
        assertExpression(t, "t")
        assertExpression(t, "'t")

        assertExpression(nil, "nil")
        assertExpression(nil, "()")
        assertExpression(nil, "'()")
        assertExpression(nil, " ( ) ")
    }

    @Test
    fun `test user-defined variables`() {
        val name = "user-defined-variable"
        try {
            eval(Reader(name).next())
        } catch (e: NoSuchElementException) {
            assertEquals(name, e.message)
        }

        val value = Atom("UserDefinedValue")
        val bindings = Bindings(name to value)
        assertExpression(value, name, bindings)
    }

    @Test
    fun `test lambda expression`() {
        val lambda = eval(Reader("(lambda (name) (cons 'Hello name))").next())
        assertTrue(lambda is Lambda)
        assertExpressionEquals(list(Atom("name")), lambda.parameters)
        assertExpressionEquals(list(
            Atom("cons"),
            list(Atom("quote"), Atom("Hello")),
            Atom("name"),
        ), lambda.body)
        assertSame(Bindings.GLOBAL, lambda.bindings)
    }

    @Test
    fun `test invoke lambda expression`() {
        assertExpression(Cons(Atom("Hello"), Atom("Joe")), """
            ((lambda (name)
               (cons 'Hello name))
             'Joe)
        """)
    }

    @Test
    fun `test lambda expression as argument`() {
        assertExpression(Cons(Atom("Hello"), Atom("Joe")), """
            ((lambda (welcome name)
               (welcome name))
             (lambda (name)
                (cons 'Hello name))
             'Joe)
        """)
    }

    @Test
    fun `test closure expression`() {
        assertExpression(Cons(Atom("Hello"), Atom("Joe")), """
            ((lambda (say hello name)
               (((say hello) name)))
             (lambda (prefix)
               (lambda (suffix)
                 (lambda ()
                   (cons prefix suffix))))
             'Hello
             'Joe)
        """)
    }

    @Test
    fun `test y-combinator`() {
        assertExpression(list(Atom("C"), Atom("B"), Atom("A")), """
            (((lambda (fn)
                ((lambda (meta)
                   (meta meta))
                 (lambda (meta-fn)
                   (fn
                     (lambda (arg1 arg2)
                       ((meta-fn meta-fn) arg1 arg2))))))
              (lambda (reverse)
                (lambda (source target)
                  (cond
                    ((eq source nil) target)
                    (t (reverse (cdr source) (cons (car source) target)))))))
             '(A B C)
             nil)
        """.trimIndent())
    }

    @Test
    fun `test quote special form`() {
        assertExpression(Atom("Joe"), "'Joe")
        assertExpression(Atom("Joe"), "(quote Joe)")
        assertExpression(list(Atom("Hello"), Atom("World")), "'(Hello World)")
    }

    @Test
    fun `test atom special form`() {
        assertExpression(t, "(atom nil)")
        assertExpression(t, "(atom ())")
        assertExpression(t, "(atom (quote nil))")
        assertExpression(t, "(atom 'atom)")
        assertExpression(nil, "(atom '(atom))")
    }

    @Test
    fun `test eq special form`() {
        assertExpression(t, "(eq nil nil)")
        assertExpression(t, "(eq nil ())")
        assertExpression(t, "(eq () nil)")
        assertExpression(t, "(eq () ())")
        assertExpression(nil, "(eq nil 'atom)")
        assertExpression(t, "(eq 'atom 'atom)")
        assertExpression(nil, "(eq '(one two) nil)")
        assertExpression(nil, "(eq '(one two) '(one two))")
    }

    @Test
    fun `test cons special form`() {
        assertExpression(Cons(Atom("one"), Atom("two")), "(cons 'one 'two)")
        assertExpression(list(Atom("one"), Atom("two")), "(cons 'one (cons 'two nil))")
    }

    @Test
    fun `test car special form`() {
        assertExpression(nil, "(car nil)")
        assertExpression(Atom("one"), "(car '(one))")
        assertExpression(Atom("one"), "(car '(one two))")
    }

    @Test
    fun `test cdr special form`() {
        assertExpression(nil, "(cdr nil)")
        assertExpression(nil, "(cdr '(one))")
        assertExpression(list(Atom("two")), "(cdr '(one two))")
        assertExpression(list(Atom("two"), Atom("three")), "(cdr '(one two three))")
    }

    @Test
    fun `test cond special form`() {
        assertExpression(nil, "(cond)")
        assertExpression(Atom("yes"), "(cond ('t 'yes))")
        assertExpression(Atom("yes"), """
            (cond
              ((eq 'one 'two) 'no)
              ((eq 'one 'one) 'yes))
        """.trimIndent())
    }

    private fun assertExpression(expected: Expression, code: String, bindings: Bindings = Bindings.GLOBAL) {
        val actual = eval(Reader(code).next(), bindings)
        assertExpressionEquals(expected, actual)
    }

    private fun assertExpressionEquals(expected: Expression, actual: Expression) =
        when (expected) {
            is Atom -> assertAtomEquals(expected, actual)
            is Cons -> assertConsEquals(expected, actual)
        }

    private fun assertAtomEquals(expected: Atom, actual: Expression) {
        assertTrue(actual is Atom)
        assertEquals(expected.name, actual.name)
    }

    private fun assertConsEquals(expected: Cons, actual: Expression) {
        assertTrue(actual is Cons)
        assertExpressionEquals(expected.car, actual.car)
        assertExpressionEquals(expected.cdr, actual.cdr)
    }
}
