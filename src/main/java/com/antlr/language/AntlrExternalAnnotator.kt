package com.antlr.language

import com.antlr.language.AnnotationIntentActionsFactory.getFix
import com.antlr.language.validation.GrammarIssue
import com.antlr.language.validation.GrammarIssuesCollector
import com.antlr.listener.AntlrListener
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import org.antlr.runtime.ANTLRFileStream
import org.antlr.runtime.CommonToken
import org.antlr.runtime.Token
import org.antlr.v4.tool.ErrorSeverity
import java.util.*

class AntlrExternalAnnotator : ExternalAnnotator<PsiFile, MutableList<GrammarIssue>>() {
    /**
     * Called first; return file
     */
    override fun collectInformation(file: PsiFile): PsiFile {
        return file
    }

    /**
     * Called 2nd; run antlr on file
     */
    override fun doAnnotate(file: PsiFile): MutableList<GrammarIssue> {
        return ApplicationManager.getApplication()
            .runReadAction<MutableList<GrammarIssue>>(Computable { GrammarIssuesCollector.collectGrammarIssues(file) }
            )
    }

    /**
     * Called 3rd
     */
    override fun apply(
        file: PsiFile,
        issues: MutableList<GrammarIssue>,
        holder: AnnotationHolder
    ) {
        for (issue in issues) {
            if (issue.offendingTokens.isEmpty()) {
                annotateFileIssue(file, holder, issue)
            } else {
                annotateIssue(file, holder, issue)
            }
        }
        if (!ApplicationManager.getApplication().isUnitTestMode) {
            if (!file.project.isDisposed) {
                file.project.messageBus.syncPublisher<AntlrListener>(AntlrListener.TOPIC)
                    .autoRefreshPreview(file.virtualFile)
            }
        }
    }

    private fun annotateFileIssue(file: PsiFile, holder: AnnotationHolder, issue: GrammarIssue) {
        val annotation = holder.createWarningAnnotation(file, issue.annotation)
//        holder.newAnnotation(HighlightSeverity.WARNING,issue.annotation)
        annotation.isFileLevelAnnotation = true
    }

    private fun annotateIssue(file: PsiFile, holder: AnnotationHolder, issue: GrammarIssue) {
        for (t in issue.offendingTokens) {
            if (t is CommonToken && tokenBelongsToFile(t, file)) {
                val range = getTokenRange(t, file)
                val severity = getIssueSeverity(issue)
                annotate(holder, issue, range, severity, file)
            }
        }
    }

    private fun getIssueSeverity(issue: GrammarIssue): ErrorSeverity {
        if (issue.msg.errorType != null) {
            return issue.msg.errorType.severity
        }

        return ErrorSeverity.INFO
    }

    private fun getTokenRange(ct: CommonToken, file: PsiFile): TextRange {
        var startIndex = ct.startIndex
        var stopIndex = ct.stopIndex

        if (startIndex >= file.textLength) {
            // can happen in case of a 'mismatched input EOF' error
            stopIndex = file.textLength - 1
            startIndex = stopIndex
        }

        if (startIndex < 0) {
            // can happen on empty files, in that case we won't be able to show any error :/
            startIndex = 0
        }

        return TextRange(startIndex, stopIndex + 1)
    }

    private fun tokenBelongsToFile(t: Token, file: PsiFile): Boolean {
        val inputStream = t.inputStream
        if (inputStream is ANTLRFileStream) {
            // Not equal if the token belongs to an imported grammar
            return inputStream.sourceName == file.virtualFile.canonicalPath
        }

        return true
    }

    private fun annotate(
        holder: AnnotationHolder,
        issue: GrammarIssue,
        range: TextRange,
        severity: ErrorSeverity,
        file: PsiFile
    ) {
        val intentionAction: Optional<IntentionAction> = getFix(range, issue.msg.errorType, file)
        when (severity) {
            ErrorSeverity.ERROR, ErrorSeverity.ERROR_ONE_OFF, ErrorSeverity.FATAL -> {
                var annotationBuilder = holder.newAnnotation(HighlightSeverity.ERROR, issue.annotation).range(range)
                if (intentionAction.isPresent) {
                    annotationBuilder = annotationBuilder.newFix(intentionAction.get()).range(range).registerFix()
                }
                annotationBuilder.create()
            }

            ErrorSeverity.WARNING -> {
                var warningBuilder = holder.newAnnotation(HighlightSeverity.WARNING, issue.annotation).range(range)
                if (intentionAction.isPresent) {
                    warningBuilder = warningBuilder.newFix(intentionAction.get()).range(range).registerFix()
                }
                warningBuilder.create()
            }

            ErrorSeverity.WARNING_ONE_OFF, ErrorSeverity.INFO -> {
                /* When trying to remove the deprecation warning, you will need something like this:
AnnotationBuilder builder = holder.newAnnotation(HighlightSeverity.WEAK_WARNING, issue.getAnnotation()).range(range);
 */
                var infoBuilder = holder.newAnnotation(HighlightSeverity.INFORMATION, issue.annotation).range(range)
                if (intentionAction.isPresent) {
                    infoBuilder = infoBuilder.newFix(intentionAction.get()).range(range).registerFix()
                }
                infoBuilder.create()
            }
//            else ->
        }
    }
}
