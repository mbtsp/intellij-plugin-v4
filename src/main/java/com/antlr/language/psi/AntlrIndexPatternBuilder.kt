package com.antlr.language.psi

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.ANTLRv4TokenTypes.getTokenElementType
import com.antlr.language.AntlrFileRoot
import com.antlr.language.adaptor.AntlrToolLexerAdaptor
import com.intellij.lexer.Lexer
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.search.IndexPatternBuilder
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

class AntlrIndexPatternBuilder : IndexPatternBuilder {
    override fun getIndexingLexer(file: PsiFile): Lexer? {
        if (file is AntlrFileRoot) {
            val lexer = ANTLRv4Lexer(null)
            return AntlrToolLexerAdaptor(lexer)
        }
        return null
    }

    override fun getCommentTokenSet(file: PsiFile): TokenSet? {
        if (file is AntlrFileRoot) {
            return TokenSet.create(getTokenElementType(ANTLRv4Lexer.LINE_COMMENT))
        }
        return null
    }

    override fun getCommentStartDelta(tokenType: IElementType?): Int {
        return if (tokenType === getTokenElementType(ANTLRv4Lexer.LINE_COMMENT)) 2 else 0
    }

    override fun getCommentEndDelta(tokenType: IElementType?): Int {
        return 0
    }
}
