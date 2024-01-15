package com.antlr.plugin.refactor;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.antlr.plugin.ANTLRv4Language;
import com.antlr.plugin.psi.RuleSpecNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ANTLRv4RefactoringSupport extends RefactoringSupportProvider {

    public boolean isAvailable(@NotNull PsiElement context) {
        return context.getLanguage().isKindOf(ANTLRv4Language.INSTANCE);
    }

    // variable in-place rename only applies to elements limited to one file
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
        return element instanceof RuleSpecNode;
    }
}