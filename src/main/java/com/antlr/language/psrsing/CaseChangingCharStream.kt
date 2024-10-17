package com.antlr.language.psrsing

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.misc.Interval

class CaseChangingCharStream
/**
 * Constructs a new CaseChangingCharStream wrapping the given [CharStream] forcing
 * all characters to upper case or lower case.
 * @param stream The stream to wrap.
 * @param upper If true force each symbol to upper case, otherwise force to lower.
 */(val stream: CharStream, val upper: Boolean) : CharStream {
    override fun getText(interval: Interval?): String? {
        return stream.getText(interval)
    }

    override fun consume() {
        stream.consume()
    }

    override fun LA(i: Int): Int {
        val c = stream.LA(i)
        if (c <= 0) {
            return c
        }
        if (upper) {
            return Character.toUpperCase(c)
        }
        return Character.toLowerCase(c)
    }

    override fun mark(): Int {
        return stream.mark()
    }

    override fun release(marker: Int) {
        stream.release(marker)
    }

    override fun index(): Int {
        return stream.index()
    }

    override fun seek(index: Int) {
        stream.seek(index)
    }

    override fun size(): Int {
        return stream.size()
    }

    override fun getSourceName(): String? {
        return stream.sourceName
    }
}
