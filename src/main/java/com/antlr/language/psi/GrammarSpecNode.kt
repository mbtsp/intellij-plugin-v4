package com.antlr.language.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

class GrammarSpecNode(node: ASTNode) : ASTWrapperPsiElement(node) {
    class Factory : PsiElementFactory {
        override fun createElement(node: ASTNode): PsiElement {
            return GrammarSpecNode(node)
        }

        companion object {
            @JvmField
            var INSTANCE: Factory = Factory()
        }
    }
}
