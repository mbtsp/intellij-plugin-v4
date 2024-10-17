package com.antlr.language.fix

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.codeInsight.template.Template
import com.intellij.codeInsight.template.TemplateBuilderImpl
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.Nls
import java.util.*
import java.util.function.IntFunction
import java.util.stream.Collectors

class AddTokenDefinitionFix(textRange: TextRange?) : BaseIntentionAction() {
    private val textRange: TextRange = Objects.requireNonNull<TextRange>(textRange)

    override fun getFamilyName(): @Nls(capitalization = Nls.Capitalization.Sentence) String {
        return "ANTLR4"
    }

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        appendTokenDefAtLastLine(editor, file, project)
    }

    override fun getText(): String {
        return "Add token definition built from letter fragments."
    }

    private fun appendTokenDefAtLastLine(editor: Editor?, file: PsiFile, project: Project) {
        if (editor == null) return
        val tokenName = editor.document.getText(textRange)
        val tokenDefLeftSide = "$tokenName : "
        val tokenDefinitionExpression: String = buildTokenDefinitionExpressionText(tokenName)

        writeTokenDef(editor, project, "$tokenDefLeftSide$tokenDefinitionExpression;")

        editor.scrollingModel
            .scrollTo(LogicalPosition(editor.document.lineCount - 1, 0), ScrollType.MAKE_VISIBLE)

        val newLastLineStart = editor.document.getLineStartOffset(editor.document.lineCount - 1)
        editor.caretModel.moveToOffset(newLastLineStart)
        runTemplate(
            editor,
            project,
            tokenDefLeftSide,
            tokenDefinitionExpression,
            getRefreshedFile(editor, file, project),
            newLastLineStart
        )
    }

    private fun runTemplate(
        editor: Editor,
        project: Project,
        tokenDefLeftSide: String,
        tokenDefinitionExpression: String?,
        psiFile: PsiFile,
        newLastLineStart: Int
    ) {
        val elementAt = Objects.requireNonNull<PsiElement?>(
            psiFile.findElementAt(newLastLineStart),
            "Unable to find element at position $newLastLineStart"
        ).parent
        val template = buildTemplate(tokenDefinitionExpression, elementAt, tokenDefLeftSide.length)
        TemplateManager.getInstance(project).startTemplate(editor, template)
    }

    private fun getRefreshedFile(editor: Editor, file: PsiFile, project: Project): PsiFile {
        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        return Objects.requireNonNull<PsiFile>(
            psiDocumentManager.getPsiFile(editor.document),
            "Unable to resolve file (" + file.name + ") for document."
        )
    }

    private fun writeTokenDef(editor: Editor, project: Project, tokenDefinition: String?) {
        val lastLineOffset = editor.document.getLineEndOffset(editor.document.lineCount - 1)
        editor.document.insertString(lastLineOffset, "\n" + tokenDefinition)
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
    }

    private fun buildTemplate(
        tokenDefinitionExpression: String?,
        elementAt: PsiElement,
        tokenExprTextOffset: Int
    ): Template {
        val templateBuilder = TemplateBuilderImpl(elementAt)
        templateBuilder.replaceRange(getRange(elementAt, tokenExprTextOffset), tokenDefinitionExpression)
        return templateBuilder.buildInlineTemplate()
    }

    companion object {
        fun getRange(elementAt: PsiElement, tokenExprTextOffset: Int): TextRange {
            return TextRange(tokenExprTextOffset, elementAt.textLength - 1)
        }

        fun buildTokenDefinitionExpressionText(tokenName: String): String {
            return tokenName.uppercase(Locale.getDefault()).chars()
                .mapToObj<Char?>(IntFunction { c: Int -> c.toChar() }).map<String?> { c: Char? ->
                    getCharacterFragment(
                        c!!
                    )
                }.collect(Collectors.joining(" "))
        }

        private fun getCharacterFragment(c: Char): String? {
            val fragment: String? = c.toString()
            if (Character.isLetter(c)) {
                return fragment
            } else {
                return "'" + fragment + "'"
            }
        }
    }
}
