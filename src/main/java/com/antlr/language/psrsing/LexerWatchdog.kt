package com.antlr.language.psrsing

import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenStream

/**
 * Checks that a lexer is not stuck trying to match the same thing over and over, for example if the input grammar
 * can match an empty string. This prevents the IDE from crashing from things like [OutOfMemoryError]s.
 */
class LexerWatchdog(private val tokenStream: TokenStream, private val previewParser: PreviewParser) {
    private var currentIndex = -1
    private var iterationsOnCurrentIndex = 0

    fun checkLexerIsNotStuck() {
        if (currentIndex == tokenStream.index()) {
            iterationsOnCurrentIndex++
        } else {
            currentIndex = tokenStream.index()
            iterationsOnCurrentIndex = 1
        }

        if (iterationsOnCurrentIndex > THRESHOLD) {
            val token = tokenStream.get(currentIndex)
            val displayName = if (token.type == Token.EOF)
                token.text
            else
                previewParser.vocabulary.getDisplayName(token.type)

            throw object : RecognitionException(
                "interpreter was killed after $THRESHOLD iterations on token '$displayName'",
                previewParser,
                tokenStream,
                previewParser.context
            ) {
                override fun getOffendingToken(): Token {
                    return token
                }
            }
        }
    }

    companion object {
        /**
         * The number of iterations on the same index after which we kill the interpreter.
         */
        private const val THRESHOLD = 50
    }
}
