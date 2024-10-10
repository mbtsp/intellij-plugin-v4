package com.antlr.language.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

class RulesNode(node: ASTNode) : ASTWrapperPsiElement(node) {
    class Factory : PsiElementFactory {
        override fun createElement(node: ASTNode): PsiElement {
            return RulesNode(node)
        }

        companion object {
            @JvmField
            var INSTANCE: Factory = Factory()
        }
    }
}
