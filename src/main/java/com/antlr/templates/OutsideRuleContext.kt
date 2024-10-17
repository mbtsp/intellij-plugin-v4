package com.antlr.templates

import com.antlr.language.ANTLRv4Parser
import com.antlr.language.psrsing.ParsingUtils
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token

class OutsideRuleContext :
    AntlrLiveTemplateContext("ANTLR_OUTSIDE-Tool", "Outside rule", AntlrGenericContext::class.java) {
    public override fun isInContext(file: PsiFile, element: PsiElement, offset: Int): Boolean {
        val tokens: CommonTokenStream = ParsingUtils.tokenizeANTLRGrammar(file.text)
        val tokenUnderCursor: Token? = ParsingUtils.getTokenUnderCursor(tokens, offset)
        if (tokenUnderCursor == null) {
            return false // sometimes happens at the eof
        }
        val tokenIndex = tokenUnderCursor.tokenIndex
        val nextRealToken: Token? = ParsingUtils.nextRealToken(tokens, tokenIndex)
        val previousRealToken: Token? = ParsingUtils.previousRealToken(tokens, tokenIndex)

        if (nextRealToken == null || previousRealToken == null) {
            return false
        }

        val previousRealTokenType = previousRealToken.type
        val nextRealTokenType = nextRealToken.type

        if (previousRealTokenType == ANTLRv4Parser.BEGIN_ACTION) {
            // make sure we're not in a rule; has to be @lexer::header {...} stuff
            val prevPrevRealToken: Token? = ParsingUtils.previousRealToken(tokens, previousRealToken.tokenIndex)
            if (prevPrevRealToken == null) {
                return false
            }
            val prevPrevPrevRealToken: Token? =
                ParsingUtils.previousRealToken(tokens, prevPrevRealToken.tokenIndex)
            if (prevPrevPrevRealToken == null) {
                return false
            }
            if (prevPrevPrevRealToken.type != ANTLRv4Parser.AT &&
                prevPrevPrevRealToken.type != ANTLRv4Parser.COLONCOLON
            ) {
                return false
            }
        }

        val okBefore =
            previousRealTokenType == ANTLRv4Parser.RBRACE || previousRealTokenType == ANTLRv4Parser.SEMI || previousRealTokenType == ANTLRv4Parser.BEGIN_ACTION
        val okAfter =
            nextRealTokenType == ANTLRv4Parser.TOKEN_REF || nextRealTokenType == ANTLRv4Parser.RULE_REF || nextRealTokenType == Token.EOF

        return okBefore && okAfter
    }
}
