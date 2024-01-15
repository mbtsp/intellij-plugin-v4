package com.antlr.plugin.psi;

import com.antlr.plugin.parser.PsiElementFactory;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class RulesNode extends ASTWrapperPsiElement {
    public RulesNode(@NotNull ASTNode node) {
        super(node);
    }

    public static class Factory implements PsiElementFactory {
        public static Factory INSTANCE = new Factory();

        @Override
        public PsiElement createElement(ASTNode node) {
            return new RulesNode(node);
        }
    }
}
