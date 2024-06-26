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

public class ParserRuleSpecNode extends RuleSpecNode {
    public static final Logger LOG = Logger.getInstance(ParserRuleSpecNode.class);

    public ParserRuleSpecNode(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public IElementType getRuleRefType() {
        return ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES.get(ANTLRv4Lexer.RULE_REF);
    }

    @Override
    public GrammarElementRefNode getNameIdentifier() {
        GrammarElementRefNode rr = PsiTreeUtil.getChildOfType(this, ParserRuleRefNode.class);
        if (rr == null) {
            LOG.error("can't find ParserRuleRefNode child of " + this.getText(), (Throwable) null);
        }
        return rr;
    }

    public static class Factory implements PsiElementFactory {
        public static Factory INSTANCE = new Factory();

        @Override
        public PsiElement createElement(ASTNode node) {
            return new ParserRuleSpecNode(node);
        }
    }
}

