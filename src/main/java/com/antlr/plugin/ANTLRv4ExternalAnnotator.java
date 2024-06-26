package com.antlr.plugin;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.antlr.plugin.actions.AnnotationIntentActionsFactory;
import com.antlr.plugin.toolwindow.PreViewToolWindow;
import com.antlr.plugin.validation.GrammarIssue;
import com.antlr.plugin.validation.GrammarIssuesCollector;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.v4.tool.ErrorSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ANTLRv4ExternalAnnotator extends ExternalAnnotator<PsiFile, List<GrammarIssue>> {

    /**
     * Called first; return file
     */
    @Override
    @Nullable
    public PsiFile collectInformation(@NotNull PsiFile file) {
        return file;
    }

    /**
     * Called 2nd; run antlr on file
     */
    @Nullable
    @Override
    public List<GrammarIssue> doAnnotate(final PsiFile file) {
        return ApplicationManager.getApplication().runReadAction((Computable<List<GrammarIssue>>) () ->
                GrammarIssuesCollector.collectGrammarIssues(file)
        );
    }

    /**
     * Called 3rd
     */
    @Override
    public void apply(@NotNull PsiFile file,
                      List<GrammarIssue> issues,
                      @NotNull AnnotationHolder holder) {
        for (GrammarIssue issue : issues) {
            if (issue.getOffendingTokens().isEmpty()) {
                annotateFileIssue(file, holder, issue);
            } else {
                annotateIssue(file, holder, issue);
            }
        }

        final ANTLRv4PluginController controller = ANTLRv4PluginController.getInstance(file.getProject());
        if (controller != null && !ApplicationManager.getApplication().isUnitTestMode()) {
            if (!file.getProject().isDisposed()) {
                file.getProject().getMessageBus().syncPublisher(PreViewToolWindow.TOPIC).autoRefreshPreview(file.getVirtualFile());
            }

        }
    }

    private void annotateFileIssue(@NotNull PsiFile file, @NotNull AnnotationHolder holder, GrammarIssue issue) {
        Annotation annotation = holder.createWarningAnnotation(file, issue.getAnnotation());
        annotation.setFileLevelAnnotation(true);
    }

    private void annotateIssue(@NotNull PsiFile file, @NotNull AnnotationHolder holder, GrammarIssue issue) {
        for (Token t : issue.getOffendingTokens()) {
            if (t instanceof CommonToken && tokenBelongsToFile(t, file)) {
                TextRange range = getTokenRange((CommonToken) t, file);
                ErrorSeverity severity = getIssueSeverity(issue);
                annotate(holder, issue, range, severity, file);
            }
        }
    }

    private ErrorSeverity getIssueSeverity(GrammarIssue issue) {
        if (issue.getMsg().getErrorType() != null) {
            return issue.getMsg().getErrorType().severity;
        }

        return ErrorSeverity.INFO;
    }

    @NotNull
    private TextRange getTokenRange(CommonToken ct, @NotNull PsiFile file) {
        int startIndex = ct.getStartIndex();
        int stopIndex = ct.getStopIndex();

        if (startIndex >= file.getTextLength()) {
            // can happen in case of a 'mismatched input EOF' error
            startIndex = stopIndex = file.getTextLength() - 1;
        }

        if (startIndex < 0) {
            // can happen on empty files, in that case we won't be able to show any error :/
            startIndex = 0;
        }

        return new TextRange(startIndex, stopIndex + 1);
    }

    private boolean tokenBelongsToFile(Token t, @NotNull PsiFile file) {
        CharStream inputStream = t.getInputStream();
        if (inputStream instanceof ANTLRFileStream) {
            // Not equal if the token belongs to an imported grammar
            return inputStream.getSourceName().equals(file.getVirtualFile().getCanonicalPath());
        }

        return true;
    }

    private void annotate(@NotNull AnnotationHolder holder, GrammarIssue issue, TextRange range, ErrorSeverity severity, PsiFile file) {
        Optional<IntentionAction> intentionAction = AnnotationIntentActionsFactory.getFix(range, issue.getMsg().getErrorType(), file);
        switch (severity) {
            case ERROR:
            case ERROR_ONE_OFF:
            case FATAL:
                AnnotationBuilder annotationBuilder = holder.newAnnotation(HighlightSeverity.ERROR, issue.getAnnotation()).range(range);
                if (intentionAction.isPresent()) {
                    annotationBuilder = annotationBuilder.newFix(intentionAction.get()).range(range).registerFix();
                }
                annotationBuilder.create();
            case WARNING:
                AnnotationBuilder warningBuilder = holder.newAnnotation(HighlightSeverity.WARNING, issue.getAnnotation()).range(range);
                if (intentionAction.isPresent()) {
                    warningBuilder = warningBuilder.newFix(intentionAction.get()).range(range).registerFix();
                }
                warningBuilder.create();

            case WARNING_ONE_OFF:
            case INFO:
			/* When trying to remove the deprecation warning, you will need something like this:
			AnnotationBuilder builder = holder.newAnnotation(HighlightSeverity.WEAK_WARNING, issue.getAnnotation()).range(range);
			 */
                AnnotationBuilder infoBuilder = holder.newAnnotation(HighlightSeverity.INFORMATION, issue.getAnnotation()).range(range);
                if (intentionAction.isPresent()) {
                    infoBuilder = infoBuilder.newFix(intentionAction.get()).range(range).registerFix();
                }
                infoBuilder.create();
            default:
                break;
        }

    }


}
