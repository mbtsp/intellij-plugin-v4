package com.antlr.language.psi

import com.intellij.psi.PsiReference
import com.intellij.psi.tree.IElementType

class LexerRuleRefNode(type: IElementType, text: CharSequence) : GrammarElementRefNode(type, text) {
    override fun getReference(): PsiReference? {
        if (this.isDeclaration) {
            return null
        }
        return GrammarElementRef(this, text)
    }

    private val isDeclaration: Boolean
        get() {
            val parent = getParent()
            return parent is LexerRuleSpecNode
                    || parent is TokenSpecNode
                    || parent is ChannelSpecNode
        }
}
