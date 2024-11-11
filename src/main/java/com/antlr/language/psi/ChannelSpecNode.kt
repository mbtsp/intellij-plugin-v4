package com.antlr.language.psi

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.AntlrTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil

/**
 * A channel defined in the `channels` section.
 */
class ChannelSpecNode(node: ASTNode) : RuleSpecNode(node) {
    public override fun getNameIdentifier(): GrammarElementRefNode? {
        return PsiTreeUtil.getChildOfType(this, LexerRuleRefNode::class.java)
    }

    override val ruleRefType: IElementType?
        get() = AntlrTokenTypes.TOKEN_ELEMENT_TYPES?.get(ANTLRv4Lexer.TOKEN_REF)
}
