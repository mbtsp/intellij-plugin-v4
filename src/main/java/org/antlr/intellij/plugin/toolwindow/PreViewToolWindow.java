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
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(Icons.getToolWindow());
        Project project = toolWindow.getProject();
        if (!project.isDisposed()) {
            ANTLRv4PluginController antlRv4PluginController = ANTLRv4PluginController.getInstance(project);
            if (antlRv4PluginController != null) {
                antlRv4PluginController.projectOpened();
            }
            project.getMessageBus().connect().subscribe(TOPIC, new PreViewListener() {
                @Override
                public void releaseEditor(PreviewState previewState) {
                    if(previewPanel!=null){
                        previewPanel.getInputPanel().releaseEditor(previewState);
                    }
                }

                @Override
                public void setStartRuleName(VirtualFile grammarFile, String startRuleName) {
                    if(previewPanel!=null) {
                        previewPanel.getInputPanel().setStartRuleName(grammarFile, startRuleName);
                    }
                }

                @Override
                public void updateParseTreeFromDoc(VirtualFile grammarFile) {
                    if(previewPanel!=null) {
                        previewPanel.updateParseTreeFromDoc(grammarFile);
                    }
                }

                @Override
                public void grammarFileSaved(VirtualFile grammarFile) {
                    if(previewPanel!=null) {
                        previewPanel.grammarFileSaved(grammarFile);
                    }
                }

                @Override
                public void grammarFileChanged(VirtualFile grammarFile) {
                    if(previewPanel!=null) {
                        previewPanel.grammarFileChanged(grammarFile);
                    }
                }

                @Override
                public void mouseEnteredGrammarEditorEvent(VirtualFile file, EditorMouseEvent event) {
                    if(previewPanel!=null) {
                        ProfilerPanel profilerPanel = previewPanel.getProfilerPanel();
                        if (profilerPanel != null) {
                            profilerPanel.mouseEnteredGrammarEditorEvent(file, event);
                        }
                    }
                }

                @Override
                public void closeGrammar(VirtualFile file) {
                    if(previewPanel!=null) {
                        previewPanel.closeGrammar(file);
                    }
                }

                @Override
                public void setEnabled(boolean enabled) {
                    if(previewPanel!=null) {
                        previewPanel.setEnabled(enabled);
                    }
                }

                @Override
                public void toolWindowHide(@Nullable Runnable runnable) {
                    if(previewPanel!=null) {
                        toolWindow.hide(runnable);
                    }
                }

                @Override
                public void onParsingCompleted(PreviewState previewState, long duration) {
                    if(previewPanel!=null) {
                        previewPanel.onParsingCompleted(previewState, duration);
                    }
                }

                @Override
                public void notifySlowParsing() {
                    if(previewPanel!=null) {
                        previewPanel.notifySlowParsing();
                    }
                }

                @Override
                public void onParsingCancelled() {
                    if(previewPanel!=null) {
                        previewPanel.onParsingCancelled();
                    }
                }

                @Override
                public void clearParseErrors() {
                    if(previewPanel!=null) {
                        previewPanel.getInputPanel().clearParseErrors();
                    }
                }

                @Override
                public void startParsing() {
                    if(previewPanel!=null) {
                        previewPanel.startParsing();
                    }
                }

                @Override
                public void autoRefreshPreview(VirtualFile virtualFile) {
                    if(previewPanel!=null) {
                        previewPanel.autoRefreshPreview(virtualFile);
                    }
                }


            });
        }
    }

}
