package com.antlr.actions

import com.antlr.util.AntlrUtil
import com.antlr.util.getGrammarFile
import com.antlr.util.getParserRuleSurroundingRef
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAware

class TestRuleAction : AnAction(), DumbAware {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        anActionEvent.project ?: return
        val grammarFile = anActionEvent.getGrammarFile() ?: return
        AntlrUtil.currentEditorFileChangedEvent(anActionEvent.project, null, grammarFile, false)
        val ruleRefNode = anActionEvent.getParserRuleSurroundingRef() ?: return
        val fileDocumentManager = FileDocumentManager.getInstance()
        val doc = fileDocumentManager.getDocument(grammarFile)
        if (doc != null) {
            fileDocumentManager.saveDocument(doc)
        }
        AntlrUtil.startRuleNameEvent(anActionEvent.project!!, virtualFile = grammarFile, ruleName = ruleRefNode.text)

    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
    override fun update(anActionEvent: AnActionEvent) {
        val grammarFile = anActionEvent.getGrammarFile()
        if (grammarFile == null) {
            anActionEvent.presentation.isEnabledAndVisible = false
            return
        }
        val ruleRefNode = anActionEvent.getParserRuleSurroundingRef()
        if (ruleRefNode == null) {
            anActionEvent.presentation.isEnabled = false
            return
        }
        anActionEvent.presentation.isVisible = true
        if (ruleRefNode.name?.get(0)?.isLowerCase() == true) {
            anActionEvent.presentation.isEnabled = true
            anActionEvent.presentation.text = "Test rule ${ruleRefNode.name}"
        } else {
            anActionEvent.presentation.isEnabled = false
        }
    }
}
