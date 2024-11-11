package com.antlr.language.psi

import com.antlr.language.ANTLRv4Parser.RULE_optionValue
import com.antlr.language.AntlrTokenTypes.RULE_ELEMENT_TYPES

class StringLiteralElement(type: com.intellij.psi.tree.IElementType, text: CharSequence) :
    com.intellij.psi.impl.source.tree.LeafPsiElement(type, text) {
    override fun getReference(): com.intellij.psi.PsiReference? {
        val parent = parent

        if (parent != null && parent.node.elementType === RULE_ELEMENT_TYPES?.get(RULE_optionValue)) {
            return StringLiteralRef(this)
        }

        return super.getReference()
    }
}
