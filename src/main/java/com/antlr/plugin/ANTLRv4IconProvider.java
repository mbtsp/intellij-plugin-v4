package com.antlr.plugin;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import com.antlr.plugin.psi.LexerRuleRefNode;
import com.antlr.plugin.psi.ModeSpecNode;
import com.antlr.plugin.psi.ParserRuleRefNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ANTLRv4IconProvider extends IconProvider {

    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        if (element instanceof LexerRuleRefNode) {
            return Icons.LEXER_RULE;
        } else if (element instanceof ParserRuleRefNode) {
            return Icons.PARSER_RULE;
        } else if (element instanceof ANTLRv4FileRoot) {
            return Icons.FILE;
        } else if (element instanceof ModeSpecNode) {
            return Icons.MODE;
        }
        return null;
    }
}
