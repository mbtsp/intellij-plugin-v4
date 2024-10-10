package com.antlr.language.psi

import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrTokenTypes.getRuleElementType
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

class AtAction(node: ASTNode) : ASTWrapperPsiElement(node) {
    val idText: String
        get() {
            val id =
                findChildByType<PsiElement?>(getRuleElementType(ANTLRv4Parser.RULE_identifier))

            return if (id == null) "<n/a>" else id.text
        }

    val actionBlockText: String
        get() {
            val actionBlock =
                findChildByType<PsiElement?>(getRuleElementType(ANTLRv4Parser.RULE_actionBlock))

            if (actionBlock != null) {
                val openingBrace = actionBlock.firstChild
                val closingBrace = actionBlock.lastChild

                return actionBlock.text
                    .substring(openingBrace.startOffsetInParent + 1, closingBrace.startOffsetInParent)
            }

            return ""
        }

    class Factory : PsiElementFactory {
        override fun createElement(node: ASTNode): PsiElement {
            return AtAction(node)
        }

        companion object {
            @JvmField
            var INSTANCE: Factory = Factory()
        }
    }
}
