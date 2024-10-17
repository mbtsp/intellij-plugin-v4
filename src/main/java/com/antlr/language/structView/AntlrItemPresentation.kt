package com.antlr.language.structView

import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrFileRoot
import com.antlr.language.AntlrTokenTypes
import com.antlr.language.psi.GrammarElementRefNode
import com.antlr.language.psi.GrammarSpecNode
import com.antlr.language.psi.ModeSpecNode
import com.antlr.language.psi.MyPsiUtils.findChildOfType
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import javax.swing.Icon

open class AntlrItemPresentation(protected val element: PsiElement) : ItemPresentation {
    override fun getLocationString(): String? {
        return null
    }

    override fun getPresentableText(): String? {
        if (element is AntlrFileRoot) {
            val node = PsiTreeUtil.findChildOfType<GrammarSpecNode?>(element, GrammarSpecNode::class.java)
            if (node != null) {
                val id = findChildOfType(node, AntlrTokenTypes.RULE_ELEMENT_TYPES[ANTLRv4Parser.RULE_identifier])
                if (id != null) {
                    return id.text
                }
            }
            return "<n/a>"
        }
        if (element is ModeSpecNode) {
            val modeId: GrammarElementRefNode? = element.getNameIdentifier()
            if (modeId != null) {
                return modeId.name
            }
            return "<n/a>"
        }
        val node = element.node
        return node.text
    }

    override fun getIcon(open: Boolean): Icon? {
        return element.getIcon(0)
    }
}
