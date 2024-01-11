package com.ssh.report

private val EXCEPTION_CLASS_CHANGED_MESSAGE = "*** exception class was changed or removed"
private val REGEX_SINGLE_LINE = Regex("\\r\\n|\\r|\\n")
private val REGEX_COMPRESS_WHITESPACE = Regex("\\s{2,}")
fun md5(msg: String): String {
    if (msg.isBlank()) {
        return "-1"
    }
    if (msg.length > 100) {
        return msg.substring(0, 100).md5()
    }
    return msg.md5()
}

fun title(message: String): String {
    return message
        .takeIf { it.isNotEmpty() && it != EXCEPTION_CLASS_CHANGED_MESSAGE }
        ?.let { ": ${it.singleLine().compressWhitespace().ellipsis(100)}" }
        ?: ""
}

private fun String.singleLine(): String = replace(REGEX_SINGLE_LINE, " ")
private fun String.compressWhitespace(): String = replace(REGEX_COMPRESS_WHITESPACE, " ")

/**
 * 如果内容长度超出指定[长度][n]，则省略超出部分，显示为”...“。
 */
private fun String.ellipsis(n: Int): String {
    require(n >= 0) { "Requested character count $n is less than zero." }
    return when {
        n == 0 -> "..."
        n < length -> "${take(n)}..."
        else -> this
    }
}
