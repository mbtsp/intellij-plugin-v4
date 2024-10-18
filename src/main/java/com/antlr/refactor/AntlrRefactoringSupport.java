package com.antlr.refactor;

import com.antlr.language.AntlrLanguage;
import com.antlr.language.psi.RuleSpecNode;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AntlrRefactoringSupport extends RefactoringSupportProvider {
    public boolean isAvailable(@NotNull PsiElement context) {
        return context.getLanguage().isKindOf(AntlrLanguage.Companion.getINSTANCE());
    }

    // variable in-place rename only applies to elements limited to one file
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
        return element instanceof RuleSpecNode;
    }
}
