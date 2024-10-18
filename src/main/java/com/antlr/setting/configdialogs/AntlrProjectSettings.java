package com.antlr.setting.configdialogs;

import com.antlr.ui.AntlrSettingPanel;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AntlrProjectSettings implements SearchableConfigurable, Disposable {
    private AntlrSettingPanel antlrSettingPanel;
    private final Project project;

    public AntlrProjectSettings(Project project) {
        this.project = project;
    }

    @Override
    public void dispose() {
        this.antlrSettingPanel = null;
    }

    @Override
    public @NotNull @NonNls String getId() {
        return "AntlrToolProjectSettings";
    }

    @Override
    public String getDisplayName() {
        return "ANTLR Tool Project Settings";
    }

    @Override
    public @Nullable @NonNls String getHelpTopic() {
        return "ANTLR Tool Project Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        this.antlrSettingPanel = new AntlrSettingPanel(this.project, AntlrGrammarProperties.PROJECT_SETTINGS_PREFIX, true);
        return this.antlrSettingPanel.$$$getRootComponent$$$();
    }

    @Override
    public boolean isModified() {
        AntlrGrammarProperties antlrGrammarProperties = AntlrToolGrammarPropertiesStore.Companion.getGrammarProperties(this.project, AntlrGrammarProperties.PROJECT_SETTINGS_PREFIX);
        if (antlrGrammarProperties == null) {
            return false;
        }
        return this.antlrSettingPanel.isModified(antlrGrammarProperties);
    }

    @Override
    public void apply() throws ConfigurationException {
        this.antlrSettingPanel.saveValues(this.project, AntlrGrammarProperties.PROJECT_SETTINGS_PREFIX);
    }

    @Override
    public void reset() {
        this.antlrSettingPanel.initData(project, AntlrGrammarProperties.PROJECT_SETTINGS_PREFIX);
    }
}
