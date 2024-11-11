package com.antlr.language.psi

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.AntlrTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil

class LexerRuleSpecNode(node: ASTNode) : RuleSpecNode(node) {
    override val ruleRefType: IElementType?
        get() = AntlrTokenTypes.TOKEN_ELEMENT_TYPES?.get(ANTLRv4Lexer.TOKEN_REF)
    override fun getNameIdentifier(): GrammarElementRefNode? {
        val tr: GrammarElementRefNode? =
            PsiTreeUtil.getChildOfType(this, LexerRuleRefNode::class.java)
        if (tr == null) {
            LOG.error("can't find LexerRuleRefNode child of " + this.text, null as Throwable?)
        }
        return tr
    }

    val isFragment: Boolean
        get() = if(AntlrTokenTypes.TOKEN_ELEMENT_TYPES?.get(ANTLRv4Lexer.FRAGMENT)!=null)  node.findChildByType(
            AntlrTokenTypes.TOKEN_ELEMENT_TYPES?.get(ANTLRv4Lexer.FRAGMENT)!!) != null else false

    class Factory : PsiElementFactory {
        override fun createElement(node: ASTNode): PsiElement {
            return LexerRuleSpecNode(node)
        }

        companion object {
            @JvmField
            var INSTANCE: Factory = Factory()
        }
    }

    companion object {
        val LOG: Logger = Logger.getInstance(LexerRuleSpecNode::class.java)
    }
}
