package com.antlr.language.fix

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.AntlrTokenTypes.getTokenElementType
import com.antlr.language.psi.MyPsiUtils.findFirstChildOfType
import com.antlr.language.psi.RuleSpecNode
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.TextExpression
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.Nls

class CreateRuleFix(private val textRange: TextRange, file: PsiFile) : BaseIntentionAction() {
    private val ruleName: String = textRange.substring(file.text)

    override fun getFamilyName(): @Nls(capitalization = Nls.Capitalization.Sentence) String {
        return "ANTLR4"
    }

    override fun getText(): @Nls(capitalization = Nls.Capitalization.Sentence) String {
        return "Create rule '$ruleName'"
    }

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val ruleName = editor.document.getText(textRange)

        prepareEditor(project, editor, file)

        val template = TemplateManager.getInstance(project).createTemplate("", "")
        template.addTextSegment("$ruleName: ")
        template.addVariable("CONTENT", TextExpression("' '"), true)
        template.addTextSegment(";")

        TemplateManager.getInstance(project).startTemplate(editor, template)
    }

    private fun prepareEditor(project: Project, editor: Editor, file: PsiFile) {
        val insertionPoint = findInsertionPoint(editor, file)
        editor.document.insertString(insertionPoint, "\n\n")

        PsiDocumentManager.getInstance(project).commitDocument(editor.document)

        editor.caretModel.moveToOffset(insertionPoint + 2)
    }

    private fun findInsertionPoint(editor: Editor, file: PsiFile): Int {
        val atRange = file.findElementAt(textRange.endOffset)
        if (atRange != null) {
            val parentRule = PsiTreeUtil.getParentOfType<RuleSpecNode?>(atRange, RuleSpecNode::class.java)

            if (parentRule != null) {
                val semi = findFirstChildOfType(parentRule, getTokenElementType(ANTLRv4Lexer.SEMI))

                if (semi != null) {
                    return semi.textOffset + 1
                }
                return parentRule.textRange.endOffset
            }
        }

        return editor.document.getLineEndOffset(editor.document.lineCount - 1)
    }
}
