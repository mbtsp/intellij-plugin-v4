package com.antlr.language.structView

import com.antlr.language.AntlrFileRoot
import com.intellij.ide.structureView.*
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class AntlrStructureViewFactory : PsiStructureViewFactory {
    /** fake a blank Treeview with a warning  */
    class DummyViewTreeElement(psiElement: PsiElement?) : PsiTreeElementBase<PsiElement?>(psiElement) {
        override fun getChildrenBase(): MutableCollection<StructureViewTreeElement?> {
            return mutableListOf<StructureViewTreeElement?>()
        }

        override fun getPresentableText(): String? {
            return "Sorry .g not supported (use .g4)"
        }
    }

    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                val grammarFile = psiFile.virtualFile
                if (grammarFile == null || !grammarFile.name.endsWith(".g4")) {
                    return StructureViewModelBase(psiFile, DummyViewTreeElement(psiFile))
                }
                return AntlrStructureViewModel(psiFile as AntlrFileRoot)
            }
        }
    }
}
