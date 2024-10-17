package com.antlr.language

import com.antlr.language.psi.LexerRuleRefNode
import com.antlr.language.psi.ModeSpecNode
import com.antlr.language.psi.ParserRuleRefNode
import com.antlr.util.Icons
import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import javax.swing.Icon

class AntlrIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, i: Int): Icon? {
        if (element is LexerRuleRefNode) {
            return Icons.LEXER_RULE
        } else if (element is ParserRuleRefNode) {
            return Icons.PARSER_RULE
        } else if (element is AntlrFileRoot) {
            return Icons.FILE
        } else if (element is ModeSpecNode) {
            return Icons.MODE
        }
        return null
    }
}
