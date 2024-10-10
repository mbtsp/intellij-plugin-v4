package com.antlr.language.psi

import com.antlr.language.psi.MyPsiUtils.createLeafFromText
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.tree.IElementType
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls

/** Root of lexer, parser rule defs  */
abstract class RuleSpecNode(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
    protected var name: String? = null // an override to input text ID

    override fun getName(): String? {
        if (name != null) return name
        val id = getNameIdentifier()
        if (id != null) {
            return id.text
        }
        return "unknown-name"
    }

    abstract override fun getNameIdentifier(): GrammarElementRefNode?

    @Throws(IncorrectOperationException::class)
    override fun setName(name: @NonNls String): PsiElement {
        /*
		From doc: "Creating a fully correct AST node from scratch is
		          quite difficult. Thus, surprisingly, the easiest way to
		          get the replacement node is to create a dummy file in the
		          custom language so that it would contain the necessary
		          node in its parse tree, build the parse tree and
		          extract the necessary node from it.
		 */
        val id = getNameIdentifier()
        val psiElement = createLeafFromText(
            project,
            context,
            name, this.ruleRefType
        )
        if (id != null && psiElement != null) {
            id.replace(psiElement)
        }
        this.name = name
        return this
    }

    abstract val ruleRefType: IElementType

    override fun subtreeChanged() {
        super.subtreeChanged()
        name = null
    }

    override fun getTextOffset(): Int {
        val id = getNameIdentifier()
        if (id != null) return id.textOffset
        return super.getTextOffset()
    }
}
