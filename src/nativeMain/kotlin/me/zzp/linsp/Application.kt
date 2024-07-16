package me.zzp.linsp

import me.zzp.linsp.helper.slurp
import me.zzp.linsp.interpreter.Bindings
import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Expression.Atom.Companion.t
import me.zzp.linsp.interpreter.Lexer
import me.zzp.linsp.interpreter.Parser

fun main(args: Array<String>) {
    if (args.size != 1) {
        error("Usage: linsp <source-file>")
    }
    val bindings = Bindings("t" to t, "nil" to nil)
    Parser(Lexer(slurp(args[0]))).forEach { println(it.eval(bindings)) }
}
