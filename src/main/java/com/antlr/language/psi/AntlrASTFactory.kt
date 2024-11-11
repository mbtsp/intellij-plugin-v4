package com.antlr.language.psi

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrTokenTypes
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTFactory
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.FileElement
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType

class AntlrASTFactory : ASTFactory() {
    companion object {
        private val ruleElementTypeToPsiFactory: MutableMap<IElementType?, PsiElementFactory?> =
            HashMap<IElementType?, PsiElementFactory?>()
        init {
            // later auto gen with tokens from some spec in grammar?
            ruleElementTypeToPsiFactory.put(
                AntlrTokenTypes.RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_rules),
                RulesNode.Factory.INSTANCE
            )
            ruleElementTypeToPsiFactory.put(
                AntlrTokenTypes.RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_parserRuleSpec),
                ParserRuleSpecNode.Factory.INSTANCE
            )
            ruleElementTypeToPsiFactory.put(
                AntlrTokenTypes.RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_lexerRule),
                LexerRuleSpecNode.Factory.INSTANCE
            )
            ruleElementTypeToPsiFactory.put(
                AntlrTokenTypes.RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_grammarSpec),
                GrammarSpecNode.Factory.INSTANCE
            )
            ruleElementTypeToPsiFactory.put(
                AntlrTokenTypes.RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_modeSpec),
                ModeSpecNode.Factory.INSTANCE
            )
            ruleElementTypeToPsiFactory.put(
                AntlrTokenTypes.RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_action),
                AtAction.Factory.INSTANCE
            )
            ruleElementTypeToPsiFactory.put(
                AntlrTokenTypes.RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_identifier),
                TokenSpecNode.Factory.INSTANCE
            )
        }

        fun createInternalParseTreeNode(node: ASTNode): PsiElement {
            val t: PsiElement
            val tokenType = node.elementType
            val factory: PsiElementFactory? = ruleElementTypeToPsiFactory[tokenType]
            t = factory?.createElement(node) ?: ASTWrapperPsiElement(node)
            return t
        }
    }
    /** Create a FileElement for root or a parse tree CompositeElement (not
     * PSI) for the token. This impl is more or less the default.
     */
    override fun createComposite(type: IElementType): CompositeElement? {
        if (type is IFileElementType) {
            return FileElement(type, null)
        }
        return CompositeElement(type)
    }

    /** Create PSI nodes out of tokens so even parse tree sees them as such.
     * Does not see whitespace tokens.
     */
    override fun createLeaf(type: IElementType, text: CharSequence): LeafElement? {
        val t = if (type === AntlrTokenTypes.TOKEN_ELEMENT_TYPES?.get(ANTLRv4Lexer.RULE_REF)) {
            ParserRuleRefNode(type, text)
        } else if (type === AntlrTokenTypes.TOKEN_ELEMENT_TYPES?.get(ANTLRv4Lexer.TOKEN_REF)) {
            LexerRuleRefNode(type, text)
        } else if (type === AntlrTokenTypes.TOKEN_ELEMENT_TYPES?.get(ANTLRv4Lexer.STRING_LITERAL)) {
            StringLiteralElement(type, text)
        } else {
            LeafPsiElement(type, text)
        }
        return t
    }


}
