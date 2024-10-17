package com.antlr.language.psi

import com.intellij.psi.PsiReference
import com.intellij.psi.tree.IElementType

class ParserRuleRefNode(type: IElementType, text: CharSequence) : GrammarElementRefNode(type, text) {
    override fun getReference(): PsiReference? {
        if (this.isDeclaration) {
            return null
        }
        return GrammarElementRef(this, text)
    }

    private val isDeclaration: Boolean
        get() = parent is ParserRuleSpecNode
}
