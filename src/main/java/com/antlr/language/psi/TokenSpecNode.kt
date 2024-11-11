package com.antlr.language.psi

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrTokenTypes
import com.antlr.language.AntlrTokenTypes.TOKEN_ELEMENT_TYPES
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil

/**
 * A token defined in the `tokens` section.
 */
class TokenSpecNode(node: ASTNode) : RuleSpecNode(node) {
    override fun getNameIdentifier(): GrammarElementRefNode? {
        return PsiTreeUtil.getChildOfType<LexerRuleRefNode?>(this, LexerRuleRefNode::class.java)
    }
    override val ruleRefType: IElementType?
        get() = TOKEN_ELEMENT_TYPES?.get(ANTLRv4Lexer.TOKEN_REF)

    class Factory : PsiElementFactory {
        override fun createElement(node: ASTNode): PsiElement {
            val idList = node.treeParent
            var parent: ASTNode? = null

            if (idList != null) {
                parent = idList.treeParent
            }
            if (parent != null) {
                if (parent.elementType === AntlrTokenTypes.RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_tokensSpec)) {
                    return TokenSpecNode(node)
                } else if (parent.elementType === AntlrTokenTypes.RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_channelsSpec)) {
                    return ChannelSpecNode(node)
                }
            }

            return ASTWrapperPsiElement(node)
        }

        companion object {
            @JvmField
            var INSTANCE: Factory = Factory()
        }
    }
}
