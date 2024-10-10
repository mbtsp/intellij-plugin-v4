package com.antlr.language

import com.antlr.language.AntlrFileLanguage.Companion.INSTANCE
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory
import org.antlr.intellij.adaptor.lexer.RuleIElementType
import org.antlr.intellij.adaptor.lexer.TokenIElementType
import org.intellij.lang.annotations.MagicConstant

object ANTLRv4TokenTypes {
    var BAD_TOKEN_TYPE: IElementType = IElementType("BAD_TOKEN", INSTANCE)
    val TOKEN_ELEMENT_TYPES: MutableList<TokenIElementType> =
        PSIElementTypeFactory.getTokenIElementTypes(INSTANCE)
    val RULE_ELEMENT_TYPES: MutableList<RuleIElementType> =
        PSIElementTypeFactory.getRuleIElementTypes(INSTANCE)

    val COMMENTS: TokenSet = PSIElementTypeFactory.createTokenSet(
        INSTANCE,
        ANTLRv4Lexer.DOC_COMMENT,
        ANTLRv4Lexer.BLOCK_COMMENT,
        ANTLRv4Lexer.LINE_COMMENT
    )

    val WHITESPACES: TokenSet = PSIElementTypeFactory.createTokenSet(
        INSTANCE,
        ANTLRv4Lexer.WS
    )

    val KEYWORDS: TokenSet = PSIElementTypeFactory.createTokenSet(
        INSTANCE,
        ANTLRv4Lexer.LEXER, ANTLRv4Lexer.PROTECTED, ANTLRv4Lexer.IMPORT, ANTLRv4Lexer.CATCH,
        ANTLRv4Lexer.PRIVATE, ANTLRv4Lexer.FRAGMENT, ANTLRv4Lexer.PUBLIC, ANTLRv4Lexer.MODE,
        ANTLRv4Lexer.FINALLY, ANTLRv4Lexer.RETURNS, ANTLRv4Lexer.THROWS, ANTLRv4Lexer.GRAMMAR,
        ANTLRv4Lexer.LOCALS, ANTLRv4Lexer.PARSER
    )

    fun getRuleElementType(@MagicConstant(valuesFromClass = ANTLRv4Parser::class) ruleIndex: Int): RuleIElementType {
        return RULE_ELEMENT_TYPES[ruleIndex]
    }

    fun getTokenElementType(@MagicConstant(valuesFromClass = ANTLRv4Lexer::class) ruleIndex: Int): TokenIElementType {
        return TOKEN_ELEMENT_TYPES[ruleIndex]
    }
}
