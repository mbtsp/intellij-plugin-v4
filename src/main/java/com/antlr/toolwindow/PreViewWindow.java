package com.antlr.toolwindow;

import com.antlr.ui.view.PreviewPanel;
import com.antlr.util.Icons;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

public class PreViewWindow implements ToolWindowFactory, DumbAware {
    private PreviewPanel previewPanel;
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.previewPanel = new PreviewPanel(project);

        Content content =  toolWindow.getContentManager().getFactory().createContent(previewPanel, null, false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content, true);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(Icons.getToolWindow());
    }
}
