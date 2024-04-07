package com.antlr.plugin.configdialogs;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stores code generation preferences in <code>.idea/misc.xml</code>.
 */
@State(name = "ANTLRNewGenerationPreferences")
public class ANTLRv4ToolGrammarPropertiesComponent implements PersistentStateComponent<ANTLRv4ToolGrammarPropertiesStore> {

    private ANTLRv4ToolGrammarPropertiesStore mySettings = new ANTLRv4ToolGrammarPropertiesStore();
    @Nullable
    public static ANTLRv4ToolGrammarPropertiesComponent getInstance(Project project) {
        if(project.isDisposed()){
            return null;
        }
        return project.getService(ANTLRv4ToolGrammarPropertiesComponent.class);
    }

    @NotNull
    @Override
    public ANTLRv4ToolGrammarPropertiesStore getState() {
        return mySettings;
    }

    @Override
    public void loadState(ANTLRv4ToolGrammarPropertiesStore state) {
        mySettings = state;
    }
}
