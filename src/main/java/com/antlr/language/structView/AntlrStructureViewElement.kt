package com.antlr.language.structView

import com.antlr.language.AntlrFileRoot
import com.antlr.language.psi.*
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.util.PsiTreeUtil

class AntlrStructureViewElement(private val element: PsiElement?) : StructureViewTreeElement {
    override fun getValue(): Any? {
        return element
    }

    override fun navigate(requestFocus: Boolean) {
        if (element is NavigationItem) {
            (element as NavigationItem).navigate(requestFocus)
        }
    }

    override fun canNavigate(): Boolean {
        return element is NavigationItem &&
                (element as NavigationItem).canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return element is NavigationItem &&
                (element as NavigationItem).canNavigateToSource()
    }

    override fun getPresentation(): ItemPresentation {
        return AntlrItemPresentation(element!!)
    }

    override fun getChildren(): Array<TreeElement?> {
        val treeElements: MutableList<TreeElement?> = ArrayList<TreeElement?>()

        if (element is AntlrFileRoot) {
            object : PsiRecursiveElementVisitor() {
                override fun visitElement(element: PsiElement) {
                    if (element is ModeSpecNode) {
                        treeElements.add(AntlrStructureViewElement(element))
                        return
                    }

                    if (element is LexerRuleSpecNode || element is ParserRuleSpecNode) {
                        val rule: PsiElement? = PsiTreeUtil.findChildOfAnyType<GrammarElementRefNode?>(
                            element,
                            LexerRuleRefNode::class.java,
                            ParserRuleRefNode::class.java
                        )
                        if (rule != null) {
                            treeElements.add(AntlrStructureViewElement(rule))
                        }
                    }

                    super.visitElement(element)
                }
            }.visitElement(element)
        } else if (element is ModeSpecNode) {
            val lexerRules = PsiTreeUtil.getChildrenOfType<LexerRuleSpecNode?>(element, LexerRuleSpecNode::class.java)

            if (lexerRules != null) {
                for (lexerRule in lexerRules) {
                    treeElements.add(
                        AntlrStructureViewElement(
                            PsiTreeUtil.findChildOfType<LexerRuleRefNode?>(
                                lexerRule,
                                LexerRuleRefNode::class.java
                            )
                        )
                    )
                }
            }
        }

        return treeElements.toTypedArray<TreeElement?>()
    }

    // probably not critical
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as AntlrStructureViewElement

        return element == that.element
    }

    override fun hashCode(): Int {
        return element?.hashCode() ?: 0
    }
}
