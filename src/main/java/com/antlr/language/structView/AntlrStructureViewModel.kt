package com.antlr.language.structView

import com.antlr.language.AntlrFileRoot
import com.antlr.language.psi.LexerRuleSpecNode
import com.antlr.language.psi.ParserRuleSpecNode
import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewModel.ElementInfoProvider
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.ide.util.treeView.smartTree.SorterUtil
import com.intellij.psi.PsiFile

class AntlrStructureViewModel(var rootElement: AntlrFileRoot) :
    StructureViewModelBase(rootElement, AntlrStructureViewElement(rootElement)), ElementInfoProvider {


    override fun getSorters(): Array<Sorter> {
        return arrayOf<Sorter>(PARSER_LEXER_RULE_SORTER, Sorter.ALPHA_SORTER)
    }

    override fun getPsiFile(): PsiFile? {
        return rootElement
    }

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean {
        val value = element.value
        return value is AntlrFileRoot
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
        val value = element.value
        return value is ParserRuleSpecNode || value is LexerRuleSpecNode
    }

    /**
     * Intellij: The implementation of StructureViewTreeElement.getChildren()
     * needs to be matched by TextEditorBasedStructureViewModel.getSuitableClasses().
     * The latter method returns an array of PsiElement-derived classes which can
     * be shown as structure view elements, and is used to select the Structure
     * View item matching the cursor position when the structure view is first
     * opened or when the "Autoscroll from source" option is used.
     */
    override fun getSuitableClasses(): Array<Class<*>> {
        return arrayOf<Class<*>>(
            AntlrFileRoot::class.java,
            LexerRuleSpecNode::class.java,
            ParserRuleSpecNode::class.java
        )
    }

    companion object {
        private val PARSER_LEXER_RULE_SORTER: Sorter = object : Sorter {
            override fun getComparator(): Comparator<*> {
                return Comparator { o1: Any?, o2: Any? ->
                    var s1 = SorterUtil.getStringPresentation(o1)
                    var s2 = SorterUtil.getStringPresentation(o2)
                    // flip case of char 0 so it puts parser rules first
                    s1 = if (Character.isLowerCase(s1[0])) {
                        s1[0].uppercaseChar().toString() + s1.substring(1)
                    } else {
                        s1[0].lowercaseChar().toString() + s1.substring(1)
                    }
                    s2 = if (Character.isLowerCase(s2[0])) {
                        s2[0].uppercaseChar().toString() + s2.substring(1)
                    } else {
                        s2[0].lowercaseChar().toString() + s2.substring(1)
                    }
                    s1.compareTo(s2)
                }
            }

            override fun isVisible(): Boolean {
                return true
            }

            override fun getPresentation(): ActionPresentation {
                // how it's described in sort by dropdown in nav window.
                val name = "Sort by rule type"
                return ActionPresentationData(name, name, AllIcons.ObjectBrowser.SortByType)
            }

            override fun getName(): String {
                return "PARSER_LEXER_RULE_SORTER"
            }
        }
    }
}
