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

/**
 * The main entry point of the Lisp interpreter.
 */
object Application : CliktCommand(
    name = "linsp",
    help = "A lightweight Lisp interpreter designed for cross-platform compatibility using Kotlin/Native.",
    epilog = "E-mail bug reports to: <redraiment@gmail.com>."
) {

    /**
     * Holds the source code read from the provided script file.
     */
    private val code: String by argument("script-file")
        .convert {
            SystemFileSystem.source(Path(it)).use { source ->
                source.buffered().use { buffer ->
                    buffer.readString()
                }
            }
        }

    /**
     * Initializes the environment with predefined bindings and evaluates
     * the parsed expressions from the source code.
     */
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

/**
 * Execute the command-line interface with the given arguments.
 */
fun main(args: Array<String>) = Application
    .versionOption("1.0.0")
    .main(args)
