package com.antlr.plugin.templates;

import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class ANTLRGenericContext extends ANTLRLiveTemplateContext {
    public ANTLRGenericContext() {
        super("ANTLR-Tool", "ANTLR-Tool", EverywhereContextType.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiFile file, @NotNull PsiElement element, int offset) {
        return false;
    }
}
