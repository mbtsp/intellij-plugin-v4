package com.antlr.language.editor

import com.antlr.language.ANTLRv4Lexer.*
import com.antlr.language.ANTLRv4TokenTypes.getTokenElementType
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class AntlrBraceMatcher : PairedBraceMatcher {
    override fun getPairs(): Array<BracePair> {
        return arrayOf<BracePair>(
            BracePair(getTokenElementType(LPAREN), getTokenElementType(RPAREN), false),
            BracePair(getTokenElementType(OPTIONS), getTokenElementType(RBRACE), true),
            BracePair(getTokenElementType(TOKENS), getTokenElementType(RBRACE), true),
            BracePair(getTokenElementType(CHANNELS), getTokenElementType(RBRACE), true),
            BracePair(getTokenElementType(BEGIN_ACTION), getTokenElementType(END_ACTION), false),
            BracePair(getTokenElementType(BEGIN_ARGUMENT), getTokenElementType(END_ARGUMENT), false),
            BracePair(getTokenElementType(LT), getTokenElementType(GT), false),
        )
    }

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean {
        return true
    }

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int {
        return openingBraceOffset
    }
}
