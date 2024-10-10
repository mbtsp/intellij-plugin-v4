package com.antlr.language


import com.antlr.language.adaptor.AntlrToolLexerAdaptor
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class AntlrSyntaxHighlighter: SyntaxHighlighterBase() {
    companion object {
        val KEY_WORD=createTextAttributesKey("Antlr_KEY_WORD", DefaultLanguageHighlighterColors.KEYWORD)

        val RULE_NAME =
            createTextAttributesKey("ANTLRv4_RULENAME", DefaultLanguageHighlighterColors.PARAMETER)
        val  TOKEN_NAME =
            createTextAttributesKey("ANTLRv4_TOKENNAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        val  STRING =
            createTextAttributesKey("ANTLRv4_STRING", DefaultLanguageHighlighterColors.STRING)
        val  LINE_COMMENT =
            createTextAttributesKey("ANTLRv4_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val  DOC_COMMENT =
            createTextAttributesKey("ANTLRv4_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT)
        val  BLOCK_COMMENT =
            createTextAttributesKey("ANTLRv4_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)

        val BAD_CHARKEYS = pack(HighlighterColors.BAD_CHARACTER)
        val STRING_KEYS = pack(STRING)
        val COMMENT_KEYS =  arrayOf<TextAttributesKey>(LINE_COMMENT, DOC_COMMENT, BLOCK_COMMENT)
        val EMPTY_KEYS =  emptyArray<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer {
        val lexer = ANTLRv4Lexer(null)
        return AntlrToolLexerAdaptor(lexer)
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<out TextAttributesKey?> {
        if (AntlrTokenTypes.KEYWORDS.contains(tokenType)) {
            return pack(KEY_WORD)
        }

        if (tokenType == AntlrTokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.TOKEN_REF]) {
            return pack(TOKEN_NAME)
        } else if (tokenType == AntlrTokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.RULE_REF]) {
            return pack(RULE_NAME)
        } else if (tokenType == AntlrTokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.STRING_LITERAL]
            || tokenType == AntlrTokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.UNTERMINATED_STRING_LITERAL]
        ) {
            return STRING_KEYS
        } else if (tokenType == AntlrTokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.BLOCK_COMMENT]) {
            return COMMENT_KEYS
        } else if (tokenType == AntlrTokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.DOC_COMMENT]) {
            return COMMENT_KEYS
        } else if (tokenType == AntlrTokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.LINE_COMMENT]) {
            return COMMENT_KEYS
        } else if (tokenType == AntlrTokenTypes.BAD_TOKEN_TYPE) {
            return BAD_CHARKEYS
        } else {
            return EMPTY_KEYS
        }
    }

}
