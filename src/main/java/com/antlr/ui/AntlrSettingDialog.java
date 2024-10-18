package com.antlr.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import org.antlr.v4.Tool;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AntlrSettingDialog extends DialogWrapper {
    private AntlrSettingPanel antlrSettingPanel;
    private final VirtualFile virtualFile;
    private final Project project;

    protected AntlrSettingDialog(@Nullable Project project, VirtualFile virtualFile, boolean isSetting) {
        super(project, true);
        this.virtualFile = virtualFile;
        this.project = project;
        antlrSettingPanel = new AntlrSettingPanel(project, virtualFile.getPath(), isSetting);
        setSize(700, 390);
        setTitle("Configure ANTLR Tool " + Tool.VERSION + " for " + virtualFile.getName());
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return antlrSettingPanel.$$$getRootComponent$$$();
    }

    @Override
    protected void doOKAction() {
        antlrSettingPanel.saveValues(project, virtualFile.getPath());
        super.doOKAction();
    }
}
