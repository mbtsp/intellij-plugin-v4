package com.antlr.language.psi

import com.antlr.language.resolve.TokenVocabResolver.resolveTokenVocabFile
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.ArrayUtilRt

class StringLiteralRef(node: StringLiteralElement) :
    PsiReferenceBase<StringLiteralElement?>(node, TextRange.from(1, node.textLength - 2)) {
    // For compatibility with 2017.x
    override fun getVariants(): Array<Any?> {
        return ArrayUtilRt.EMPTY_OBJECT_ARRAY
    }

    override fun resolve(): PsiElement? {
        return resolveTokenVocabFile(myElement)
    }
}
