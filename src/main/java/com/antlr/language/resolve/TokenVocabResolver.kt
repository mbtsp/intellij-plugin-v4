package com.antlr.language.resolve

import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrFileRoot
import com.antlr.language.AntlrTokenTypes.RULE_ELEMENT_TYPES
import com.antlr.language.psi.GrammarElementRefNode
import com.antlr.language.psi.GrammarSpecNode
import com.antlr.language.psi.LexerRuleSpecNode
import com.antlr.language.psi.MyPsiUtils.findSpecNode
import com.antlr.language.psi.MyPsiUtils.findTokenVocabIfAny
import com.antlr.language.psi.TokenSpecNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.lang3.StringUtils

object TokenVocabResolver {
    /**
     * If this reference is the value of a `tokenVocab` option, returns the corresponding
     * grammar file.
     */
    fun resolveTokenVocabFile(reference: PsiElement?): PsiFile? {
        val optionValue =
            PsiTreeUtil.findFirstParent(reference, TokenVocabResolver::isOptionValue)

        if (optionValue != null) {
            val option = optionValue.parent

            if (option != null) {
                val optionName = PsiTreeUtil.getDeepestFirst(option)

                if (optionName.text == "tokenVocab") {
                    val text = StringUtils.strip(reference!!.text, "'")
                    return findRelativeFile(text, reference.containingFile)
                }
            }
        }

        return null
    }

    /**
     * Tries to find a declaration named `ruleName` in the `tokenVocab` file if it exists.
     */
    fun resolveInTokenVocab(reference: GrammarElementRefNode, ruleName: String?): PsiElement? {
        val tokenVocab = findTokenVocabIfAny(reference.containingFile as AntlrFileRoot?)

        if (tokenVocab != null) {
            val tokenVocabFile = findRelativeFile(tokenVocab, reference.containingFile)

            if (tokenVocabFile != null) {
                val lexerGrammar =
                    PsiTreeUtil.findChildOfType<GrammarSpecNode?>(tokenVocabFile, GrammarSpecNode::class.java)
                val node = findSpecNode(lexerGrammar, ruleName)

                if (node is LexerRuleSpecNode) {
                    // fragments are not visible to the parser
                    if (!node.isFragment) {
                        return node
                    }
                }
                if (node is TokenSpecNode) {
                    return node
                }
            }
        }

        return null
    }

    private fun isOptionValue(el: PsiElement): Boolean {
        val node = el.node
        return node != null && node.elementType === RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_optionValue)
    }

    /**
     * Looks for an ANTLR grammar file named `<baseName>`.g4 next to the given `sibling` file.
     */
    fun findRelativeFile(baseName: String?, sibling: PsiFile): PsiFile? {
        val parentDirectory = sibling.parent

        if (parentDirectory != null) {
            val candidate = parentDirectory.findFile("$baseName.g4")

            if (candidate is AntlrFileRoot) {
                return candidate
            }
        }

        return null
    }
}
