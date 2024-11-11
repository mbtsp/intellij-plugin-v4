package com.antlr.language.psi

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.AntlrTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil

class ParserRuleSpecNode(node: ASTNode) : RuleSpecNode(node) {
    override val ruleRefType: IElementType?
        get() = AntlrTokenTypes.TOKEN_ELEMENT_TYPES?.get(ANTLRv4Lexer.RULE_REF)
    override fun getNameIdentifier(): GrammarElementRefNode? {
        val rr: GrammarElementRefNode? =
            PsiTreeUtil.getChildOfType<ParserRuleRefNode?>(this, ParserRuleRefNode::class.java)
        if (rr == null) {
            LOG.error("can't find ParserRuleRefNode child of " + this.text, null as Throwable?)
        }
        return rr
    }

    class Factory : PsiElementFactory {
        override fun createElement(node: ASTNode): PsiElement {
            return ParserRuleSpecNode(node)
        }

        companion object {
            @JvmField
            var INSTANCE: Factory = Factory()
        }
    }

    companion object {
        val LOG: Logger = Logger.getInstance(ParserRuleSpecNode::class.java)
    }
}

