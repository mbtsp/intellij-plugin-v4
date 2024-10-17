package com.antlr.templates

import com.antlr.language.AntlrLanguage
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilBase
import org.jetbrains.annotations.NonNls

abstract class AntlrLiveTemplateContext(
    id: @NonNls String,
    presentableName: String,
    baseContextType: Class<out TemplateContextType?>?
) : TemplateContextType(id, presentableName, baseContextType) {
    protected abstract fun isInContext(file: PsiFile, element: PsiElement, offset: Int): Boolean

    override fun isInContext(file: PsiFile, offset: Int): Boolean {
        // offset is where cursor or insertion point is I guess
        var offset = offset
        if (!PsiUtilBase.getLanguageAtOffset(file, offset).isKindOf(AntlrLanguage.INSTANCE)) {
            return false
        }
        if (offset == file.textLength) { // allow at EOF
            offset--
        }
        val element = file.findElementAt(offset)

        if (element == null) {
            return false
        }

        return isInContext(file, element, offset)
    }
}
