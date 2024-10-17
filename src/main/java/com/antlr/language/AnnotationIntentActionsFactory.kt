package com.antlr.language

import com.antlr.language.fix.AddTokenDefinitionFix
import com.antlr.language.fix.CreateRuleFix
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import org.antlr.v4.tool.ErrorType
import java.util.*

object AnnotationIntentActionsFactory {
    @JvmStatic
    fun getFix(textRange: TextRange, errorType: ErrorType?, file: PsiFile): Optional<IntentionAction> {
        if (errorType == ErrorType.IMPLICIT_TOKEN_DEFINITION) {
            return Optional.of<IntentionAction>(AddTokenDefinitionFix(textRange))
        } else if (errorType == ErrorType.UNDEFINED_RULE_REF) {
            return Optional.of<IntentionAction>(CreateRuleFix(textRange, file))
        }
        return Optional.empty<IntentionAction>()
    }
}
