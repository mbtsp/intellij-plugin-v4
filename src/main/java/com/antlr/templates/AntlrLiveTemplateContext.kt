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
        var p0 = offset
        if (!PsiUtilBase.getLanguageAtOffset(file, p0).isKindOf(AntlrLanguage.INSTANCE)) {
            return false
        }
        if (p0 == file.textLength) { // allow at EOF
            p0--
        }
        val element = file.findElementAt(p0) ?: return false

        return isInContext(file, element, p0)
    }
}
