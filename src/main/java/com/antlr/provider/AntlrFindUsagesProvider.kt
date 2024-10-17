package com.antlr.provider

import com.antlr.language.psi.*
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class AntlrFindUsagesProvider : FindUsagesProvider {
    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is RuleSpecNode
    }

    override fun getWordsScanner(): WordsScanner? {
        return null // seems ok as JavaFindUsagesProvider does same thing
    }

    override fun getHelpId(element: PsiElement): String? {
        return null
    }

    override fun getType(element: PsiElement): String {
        if (element is ParserRuleSpecNode) {
            return "parser rule"
        }
        if (element is LexerRuleSpecNode) {
            return "lexer rule"
        }
        if (element is ModeSpecNode) {
            return "mode"
        }
        if (element is TokenSpecNode) {
            return "token"
        }
        if (element is ChannelSpecNode) {
            return "channel"
        }
        return "n/a"
    }

    override fun getDescriptiveName(element: PsiElement): String {
        val rule: PsiElement? = PsiTreeUtil.findChildOfAnyType<GrammarElementRefNode?>(
            element,
            LexerRuleRefNode::class.java,
            ParserRuleRefNode::class.java
        )
        if (rule != null) return rule.text
        return "n/a"
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return getDescriptiveName(element)
    }
}
