package com.antlr.language.resolve

import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrTokenTypes.RULE_ELEMENT_TYPES
import com.antlr.language.psi.GrammarElementRefNode
import com.antlr.language.psi.GrammarSpecNode
import com.antlr.language.psi.MyPsiUtils.findSpecNode
import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.util.PsiTreeUtil

object ImportResolver {
    fun resolveImportedFile(reference: GrammarElementRefNode?): PsiFile? {
        val importStatement =
            PsiTreeUtil.findFirstParent(reference, ImportResolver::isImportStatement)

        if (importStatement != null) {
            return TokenVocabResolver.findRelativeFile(reference!!.text, reference.containingFile)
        }

        return null
    }

    private fun isImportStatement(el: PsiElement): Boolean {
        val node = el.node
        return node != null && node.elementType === RULE_ELEMENT_TYPES?.get(ANTLRv4Parser.RULE_delegateGrammar)
    }

    fun resolveInImportedFiles(grammarFile: PsiFile, ruleName: String): PsiElement? {
        return resolveInImportedFiles(grammarFile, ruleName, ArrayList<PsiFile?>())
    }

    private fun resolveInImportedFiles(
        grammarFile: PsiFile,
        ruleName: String?,
        visitedFiles: MutableList<PsiFile?>
    ): PsiElement? {
        val visitor = DelegateGrammarsVisitor()
        grammarFile.accept(visitor)

        for (importedGrammar in visitor.importedGrammars) {
            if (visitedFiles.contains(importedGrammar)) {
                continue
            }
            visitedFiles.add(importedGrammar)

            val grammar = PsiTreeUtil.getChildOfType<GrammarSpecNode?>(importedGrammar, GrammarSpecNode::class.java)
            var specNode = findSpecNode(grammar, ruleName)

            if (specNode != null) {
                return specNode
            }

            // maybe the imported grammar also imports other grammars itself?
            specNode = resolveInImportedFiles(importedGrammar, ruleName, visitedFiles)
            if (specNode != null) {
                return specNode
            }
        }

        return null
    }

    private class DelegateGrammarsVisitor : PsiRecursiveElementVisitor() {
        var importedGrammars: MutableList<PsiFile> = ArrayList<PsiFile>()

        override fun visitElement(element: PsiElement) {
            if (isImportStatement(element)) {
                val importedGrammar =
                    TokenVocabResolver.findRelativeFile(element.text, element.containingFile)

                if (importedGrammar != null) {
                    importedGrammars.add(importedGrammar)
                }
            }
            super.visitElement(element)
        }
    }
}
