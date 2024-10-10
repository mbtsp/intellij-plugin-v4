package com.antlr.language.psi

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrTokenTypes
import com.antlr.language.psi.MyPsiUtils.findFirstChildOfType
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType

/**
 * A node representing a lexical `mode` definition, and all its child rules.
 *
 * @implNote this is technically not a 'rule', but it has the same characteristics as a named rule so we
 * can extend `RuleSpecNode`
 */
class ModeSpecNode(node: ASTNode) : RuleSpecNode(node) {
    override val ruleRefType: IElementType
        get() = AntlrTokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.TOKEN_REF]

    override fun getNameIdentifier(): GrammarElementRefNode? {
        val idNode: PsiElement? =
            findFirstChildOfType(this, AntlrTokenTypes.getRuleElementType(ANTLRv4Parser.RULE_identifier))

        if (idNode != null) {
            val firstChild = idNode.firstChild

            if (firstChild is GrammarElementRefNode) {
                return firstChild
            }
        }

        return null
    }

    class Factory : PsiElementFactory {
        override fun createElement(node: ASTNode): PsiElement {
            return ModeSpecNode(node)
        }

        companion object {
            var INSTANCE: Factory = Factory()
        }
    }
}
