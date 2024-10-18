package com.antlr.actions

import com.antlr.util.getGrammarFile
import com.antlr.util.isAntlrFile
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager

class GenerateParserAction : AnAction(), DumbAware {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val project = anActionEvent.getData<Project>(PlatformDataKeys.PROJECT) ?: return
        val model = object : Task.Modal(project, "Generate Code", true) {
            override fun run(p0: ProgressIndicator) {
                p0.isIndeterminate = true
                val virtualFile = anActionEvent.getGrammarFile()
                if (virtualFile == null) {
                    return
                }
                val psiDocumentManager = PsiDocumentManager.getInstance(project)
                val fileDocumentManager = FileDocumentManager.getInstance()
                val doc =fileDocumentManager.getDocument(virtualFile)?:return
                val unsaved = !psiDocumentManager.isCommitted(doc)|| fileDocumentManager.isDocumentUnsaved(doc)
                if(unsaved){
                    psiDocumentManager.commitDocument(doc)
                    fileDocumentManager.saveDocument(doc)
                }
                
            }

        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.isAntlrFile()
    }


}
