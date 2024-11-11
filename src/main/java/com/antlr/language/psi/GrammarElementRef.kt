package com.antlr.language.psi

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.AntlrTokenTypes
import com.antlr.language.psi.MyPsiUtils.findSpecNode
import com.antlr.language.resolve.ImportResolver.resolveImportedFile
import com.antlr.language.resolve.ImportResolver.resolveInImportedFiles
import com.antlr.language.resolve.TokenVocabResolver.resolveInTokenVocab
import com.antlr.language.resolve.TokenVocabResolver.resolveTokenVocabFile
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException


/**
 * A reference to a grammar element (parser rule, lexer rule or lexical mode).
 */
class GrammarElementRef(idNode: GrammarElementRefNode, private val ruleName: String) :
    PsiReferenceBase<GrammarElementRefNode>(idNode, TextRange(0, ruleName.length)) {
    /**
     * Using for completion. Returns list of rules and tokens; the prefix
     * of current element is used as filter by IDEA later.
     */
    override fun getVariants(): Array<Any?> {
        val rules = PsiTreeUtil.getContextOfType<RulesNode?>(myElement, RulesNode::class.java)
        // find all rule defs (token, parser)
        val ruleSpecNodes =
            PsiTreeUtil.findChildrenOfAnyType<RuleSpecNode?>(
                rules,
                ParserRuleSpecNode::class.java,
                LexerRuleSpecNode::class.java
            )

        return ruleSpecNodes.toTypedArray()
    }

    /**
     * Called upon jump to def for this rule ref
     */
    override fun resolve(): PsiElement? {
        val tokenVocabFile = resolveTokenVocabFile(element)

        if (tokenVocabFile != null) {
            return tokenVocabFile
        }

        val importedFile = resolveImportedFile(element)
        if (importedFile != null) {
            return importedFile
        }

        val grammar = PsiTreeUtil.getContextOfType<GrammarSpecNode?>(element, GrammarSpecNode::class.java)
        var specNode = findSpecNode(grammar, ruleName)

        if (specNode != null) {
            return specNode
        }

        // Look for a rule defined in an imported grammar
        specNode = resolveInImportedFiles(element.containingFile, ruleName)

        if (specNode != null) {
            return specNode
        }

        // Look for a lexer rule in the tokenVocab file if it exists
        if (element is LexerRuleRefNode) {
            return resolveInTokenVocab(element, ruleName)
        }

        return null
    }

    @Throws(IncorrectOperationException::class)
    override fun handleElementRename(newElementName: String): PsiElement {
        val type = AntlrTokenTypes.TOKEN_ELEMENT_TYPES?.get(ANTLRv4Lexer.TOKEN_REF) ?: return myElement
        val project = element.project
        myElement.replace(
            MyPsiUtils.createLeafFromText(
                project,
                myElement.context,
                newElementName,
                type
                )!!
        )
        return myElement
    }

    @Throws(IncorrectOperationException::class)
    override fun bindToElement(element: PsiElement): PsiElement {
        return getElement()
    }
}
