package com.antlr.plugin.psi;

import com.antlr.plugin.parser.PsiElementFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.antlr.plugin.ANTLRv4TokenTypes;
import com.antlr.plugin.parser.ANTLRv4Lexer;
import com.antlr.plugin.parser.ANTLRv4Parser;
import org.jetbrains.annotations.NotNull;

import static com.antlr.plugin.psi.MyPsiUtils.findFirstChildOfType;


/**
 * A node representing a lexical {@code mode} definition, and all its child rules.
 *
 * @implNote this is technically not a 'rule', but it has the same characteristics as a named rule so we
 * can extend {@code RuleSpecNode}
 */
public class ModeSpecNode extends RuleSpecNode {

    public ModeSpecNode(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public IElementType getRuleRefType() {
        return ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES.get(ANTLRv4Lexer.TOKEN_REF);
    }

    @Override
    public GrammarElementRefNode getNameIdentifier() {
        PsiElement idNode = findFirstChildOfType(this, ANTLRv4TokenTypes.getRuleElementType(ANTLRv4Parser.RULE_identifier));

        if (idNode != null) {
            PsiElement firstChild = idNode.getFirstChild();

            if (firstChild instanceof GrammarElementRefNode) {
                return (GrammarElementRefNode) firstChild;
            }
        }

        return null;
    }

    public static class Factory implements PsiElementFactory {
        public static Factory INSTANCE = new Factory();

        @Override
        public PsiElement createElement(ASTNode node) {
            return new ModeSpecNode(node);
        }
    }
}