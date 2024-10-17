package com.antlr.setting.configdialogs

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(name = "AntlrToolGrammarProperties", storages = [Storage(value = "AntlrToolGrammarProperties.xml")])
class AntlrToolGrammarPropertiesComponent : PersistentStateComponent<AntlrToolGrammarPropertiesStore?> {
    private var mySettings = AntlrToolGrammarPropertiesStore()

    override fun getState(): AntlrToolGrammarPropertiesStore {
        return mySettings
    }

    override fun loadState(state: AntlrToolGrammarPropertiesStore) {
        mySettings = state
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): AntlrToolGrammarPropertiesComponent? {
            if (project.isDisposed) {
                return null
            }
            return project.getService<AntlrToolGrammarPropertiesComponent?>(AntlrToolGrammarPropertiesComponent::class.java)
        }
    }
}
