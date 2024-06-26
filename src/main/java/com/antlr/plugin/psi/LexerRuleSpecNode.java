package com.antlr.plugin.psi;

import com.antlr.plugin.parser.PsiElementFactory;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.antlr.plugin.ANTLRv4TokenTypes;
import com.antlr.plugin.parser.ANTLRv4Lexer;
import org.jetbrains.annotations.NotNull;

public class LexerRuleSpecNode extends RuleSpecNode {
    public static final Logger LOG = Logger.getInstance(LexerRuleSpecNode.class);

    public LexerRuleSpecNode(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public IElementType getRuleRefType() {
        return ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES.get(ANTLRv4Lexer.TOKEN_REF);
    }

    @Override
    public GrammarElementRefNode getNameIdentifier() {
        GrammarElementRefNode tr = PsiTreeUtil.getChildOfType(this, LexerRuleRefNode.class);
        if (tr == null) {
            LOG.error("can't find LexerRuleRefNode child of " + this.getText(), (Throwable) null);
        }
        return tr;
    }

    public boolean isFragment() {
        return getNode().findChildByType(ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES.get(ANTLRv4Lexer.FRAGMENT)) != null;
    }

    public static class Factory implements PsiElementFactory {
        public static Factory INSTANCE = new Factory();

        @Override
        public PsiElement createElement(ASTNode node) {
            return new LexerRuleSpecNode(node);
        }
    }
}
