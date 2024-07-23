package me.zzp.linsp

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.versionOption
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import me.zzp.linsp.interpreter.Bindings
import me.zzp.linsp.interpreter.Expression.Atom.Companion.nil
import me.zzp.linsp.interpreter.Expression.Atom.Companion.t
import me.zzp.linsp.interpreter.Lexer
import me.zzp.linsp.interpreter.Parser

object Application : CliktCommand(
    name = "linsp",
    help = "A Lisp interpreter implemented in Kotlin/Native.",
    epilog = "E-mail bug reports to: <redraiment@gmail.com>."
) {

    /**
     * Source Code from script file.
     */
    private val code: String by argument("script-file")
        .convert {
            SystemFileSystem.source(Path(it)).use { source ->
                source.buffered().use { buffer ->
                    buffer.readString()
                }
            }
        }

    override fun run() {
        val bindings = Bindings(
            "t" to t,
            "nil" to nil,
        )
        Parser(Lexer(code)).forEach {
            echo(it.eval(bindings))
        }
    }
}

fun main(args: Array<String>) = Application
    .versionOption("1.0.0")
    .main(args)
