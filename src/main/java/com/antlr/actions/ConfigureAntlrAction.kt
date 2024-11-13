package com.antlr.actions



import com.antlr.ui.AntlrSettingDialog
import com.antlr.util.getGrammarFile
import com.antlr.util.isGrammar
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class ConfigureAntlrAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getGrammarFile()?:return
        val antlrSettingDialog = AntlrSettingDialog(project, file,false)
        antlrSettingDialog.show()
    }

    override fun update(e: AnActionEvent) {
        e.isGrammar()
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
