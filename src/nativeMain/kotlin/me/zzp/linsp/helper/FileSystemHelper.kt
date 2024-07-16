package me.zzp.linsp.helper

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread

/**
 * 读取文件的完整内容。
 */
@OptIn(ExperimentalForeignApi::class)
fun slurp(filename: String): String {
    val file = fopen(filename, "r") ?: error("Failed to read file($filename)")
    val content = StringBuilder()
    val buffer = ByteArray(512)
    var startIndex = 0
    while (true) {
        val capacity = (buffer.size - startIndex).toULong()
        val size = fread(buffer.refTo(startIndex), 1U, capacity, file)
        val endIndex = size.toInt() + startIndex
        if (endIndex > 0) {
            var index = 0
            while (index < endIndex) {
                val value = buffer[index].toInt()
                index += if (value and 0b10000000 == 0) {
                    1
                } else if (value and 0b11100000 == 0b11000000 && index + 1 < endIndex) {
                    2
                } else if (value and 0b11110000 == 0b11100000 && index + 2 < endIndex) {
                    3
                } else if (value and 0b11111000 == 0b11110000 && index + 3 < endIndex) {
                    4
                } else {
                    break
                }
            }
            content.append(buffer.toKString(endIndex = index))
            startIndex = endIndex - index
            if (startIndex > 0) {
                buffer.copyInto(buffer, 0, index, endIndex)
            }
        } else {
            break
        }
    }
    return content.toString().also {
        fclose(file)
    }
}
