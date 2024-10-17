package com.antlr.language.psrsing

import org.antlr.v4.runtime.CharStream

enum class CaseChangingStrategy {
    LEAVE_AS_IS {
        override fun applyTo(source: CharStream): CharStream {
            return source
        }

        override fun toString(): String {
            return "Leave as-is"
        }
    },
    FORCE_UPPERCASE {
        override fun applyTo(source: CharStream): CharStream {
            return CaseChangingCharStream(source, true)
        }

        override fun toString(): String {
            return "Transform to uppercase when lexing"
        }
    },
    FORCE_LOWERCASE {
        override fun applyTo(source: CharStream): CharStream {
            return CaseChangingCharStream(source, false)
        }

        override fun toString(): String {
            return "Transform to lowercase when lexing"
        }
    };

    abstract fun applyTo(source: CharStream): CharStream
}
