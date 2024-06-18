package me.zzp

import me.zzp.helper.slurp
import me.zzp.interpreter.Bindings
import me.zzp.interpreter.Expression.Atom.Companion.nil
import me.zzp.interpreter.Expression.Atom.Companion.t
import me.zzp.interpreter.Lexer
import me.zzp.interpreter.Parser

fun main(args: Array<String>) {
    if (args.size != 1) {
        error("Usage: linsp <source-file>")
    }
    val bindings = Bindings("t" to t, "nil" to nil)
    Parser(Lexer(slurp(args[0]))).forEach { println(it.eval(bindings)) }
}
