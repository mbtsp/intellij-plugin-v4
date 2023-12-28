package org.antlr.intellij.plugin.toolwindow;

import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.Topic;
import org.antlr.intellij.plugin.ANTLRv4PluginController;
import org.antlr.intellij.plugin.Icons;
import org.antlr.intellij.plugin.listeners.PreViewListener;
import org.antlr.intellij.plugin.preview.PreviewPanel;
import org.antlr.intellij.plugin.preview.PreviewState;
import org.antlr.intellij.plugin.profiler.ProfilerPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PreViewToolWindow implements ToolWindowFactory, DumbAware {
    private PreviewPanel previewPanel;

    public static final Topic<PreViewListener> TOPIC = new Topic<>(PreViewListener.class);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.previewPanel = new PreviewPanel(project);
        Content content = ContentFactory.getInstance().createContent(previewPanel, "", false);
        content.setHelpId("antlr.new.pre.helper");
        toolWindow.getContentManager().addContent(content);
        if (!project.isDisposed()) {
            ANTLRv4PluginController antlRv4PluginController = ANTLRv4PluginController.getInstance(project);
            if (antlRv4PluginController != null) {
                antlRv4PluginController.projectOpened();
            }
            project.getMessageBus().connect().subscribe(TOPIC, new PreViewListener() {
                @Override
                public void releaseEditor(PreviewState previewState) {
                    previewPanel.getInputPanel().releaseEditor(previewState);
                }

                @Override
                public void setStartRuleName(VirtualFile grammarFile, String startRuleName) {
                    previewPanel.getInputPanel().setStartRuleName(grammarFile, startRuleName);
                }

                @Override
                public void updateParseTreeFromDoc(VirtualFile grammarFile) {
                    previewPanel.updateParseTreeFromDoc(grammarFile);
                }

                @Override
                public void grammarFileSaved(VirtualFile grammarFile) {
                    previewPanel.grammarFileSaved(grammarFile);
                }

                @Override
                public void grammarFileChanged(VirtualFile grammarFile) {
                    previewPanel.grammarFileChanged(grammarFile);
                }

                @Override
                public void mouseEnteredGrammarEditorEvent(VirtualFile file, EditorMouseEvent event) {
                    ProfilerPanel profilerPanel = previewPanel.getProfilerPanel();
                    if (profilerPanel != null) {
                        profilerPanel.mouseEnteredGrammarEditorEvent(file, event);
                    }
                }

                @Override
                public void closeGrammar(VirtualFile file) {
                    previewPanel.closeGrammar(file);
                }

                @Override
                public void setEnabled(boolean enabled) {
                    previewPanel.setEnabled(enabled);
                }

                @Override
                public void toolWindowHide(@Nullable Runnable runnable) {
                    toolWindow.hide(runnable);
                }

                @Override
                public void onParsingCompleted(PreviewState previewState, long duration) {
                    previewPanel.onParsingCompleted(previewState, duration);
                }

                @Override
                public void notifySlowParsing() {
                    previewPanel.notifySlowParsing();
                }

                @Override
                public void onParsingCancelled() {
                    previewPanel.onParsingCancelled();
                }

                @Override
                public void clearParseErrors() {
                    previewPanel.getInputPanel().clearParseErrors();
                }

                @Override
                public void startParsing() {
                    previewPanel.startParsing();
                }

                @Override
                public void autoRefreshPreview(VirtualFile virtualFile) {
                    previewPanel.autoRefreshPreview(virtualFile);
                }

                @Override
                public void show(@Nullable Runnable runnable) {
                    toolWindow.show(runnable);
                }
            });
        }

    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(Icons.getToolWindow());
    }

    public PreviewPanel getPreviewPanel() {
        return previewPanel;
    }
}
