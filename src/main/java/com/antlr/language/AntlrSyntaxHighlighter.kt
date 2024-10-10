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
    val keyWord=createTextAttributesKey("Antlr_KEY_WORD", DefaultLanguageHighlighterColors.KEYWORD)

    val ruleName =
    createTextAttributesKey("ANTLRv4_RULENAME", DefaultLanguageHighlighterColors.PARAMETER)
    val  tokenName =
    createTextAttributesKey("ANTLRv4_TOKENNAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
    val  string =
    createTextAttributesKey("ANTLRv4_STRING", DefaultLanguageHighlighterColors.STRING)
    val  lineComment =
    createTextAttributesKey("ANTLRv4_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
    val  docComment =
    createTextAttributesKey("ANTLRv4_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT)
    val  blockComment =
    createTextAttributesKey("ANTLRv4_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)

    val badCharKeys = pack(HighlighterColors.BAD_CHARACTER)
    val stringKeys = pack(string)
    val commentKeys =  arrayOf<TextAttributesKey>(lineComment, docComment, blockComment)
    val emptyKeys =  emptyArray<TextAttributesKey>()
    override fun getHighlightingLexer(): Lexer {
        val lexer = ANTLRv4Lexer(null)
        return AntlrToolLexerAdaptor(lexer)
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<out TextAttributesKey?> {
        if (ANTLRv4TokenTypes.KEYWORDS.contains(tokenType)) {
            return pack(keyWord)
        }

        if (tokenType == ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.TOKEN_REF]) {
            return pack(tokenName)
        } else if (tokenType == ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.RULE_REF]) {
            return pack(ruleName)
        } else if (tokenType == ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.STRING_LITERAL]
            || tokenType == ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.UNTERMINATED_STRING_LITERAL]
        ) {
            return stringKeys
        } else if (tokenType == ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.BLOCK_COMMENT]) {
            return commentKeys
        } else if (tokenType == ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.DOC_COMMENT]) {
            return commentKeys
        } else if (tokenType == ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.LINE_COMMENT]) {
            return commentKeys
        } else if (tokenType == ANTLRv4TokenTypes.BAD_TOKEN_TYPE) {
            return badCharKeys
        } else {
            return emptyKeys
        }
    }

}
