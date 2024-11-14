package com.antlr.actions

import com.antlr.service.AntlrService
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class CancelParserAction : AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        p0.project?.let { project ->
            project.getService(AntlrService::class.java).cancelParsing()
        }
    }

    override fun update(p0: AnActionEvent) {
        if (p0.project == null) return
        p0.presentation.isEnabled =
            PropertiesComponent.getInstance(p0.project!!).getBoolean("ANTLR_PARSE_GRAMMAR_CANCEL_ID")
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
