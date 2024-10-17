package com.antlr.language.psrsing

import org.antlr.v4.runtime.CommonToken
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenSource

/**
 * This TokenStream is just a [CommonTokenStream] that can be
 * cut off at a particular index, such as the cursor in an IDE. I
 * had to override more than I wanted to get this to work, but it seems okay.
 *
 *
 * All parsers used within the plug-in should use token streams of this type.
 */
open class TokenStreamSubset(tokenSource: TokenSource) : CommonTokenStream(tokenSource) {
    //	protected int indexOfLastToken = -1;
    protected var saveToken: Token? = null

    fun setIndexOfLastToken(indexOfLastToken: Int) {
        if (indexOfLastToken < 0) {
            tokens[saveToken!!.tokenIndex] = saveToken
            return
        }
        val i = indexOfLastToken + 1 // we want to keep token at indexOfLastToken
        sync(i)
        saveToken = tokens[i]
        val stopToken = CommonToken(saveToken)
        stopToken.setType(STOP_TOKEN_TYPE)
        tokens[i] = stopToken
    }

    companion object {
        const val STOP_TOKEN_TYPE: Int = -3
    }
}
