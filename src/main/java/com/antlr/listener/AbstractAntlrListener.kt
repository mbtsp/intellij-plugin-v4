package com.antlr.listener

import com.antlr.preview.PreviewState
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.vfs.VirtualFile

abstract class AbstractAntlrListener : AntlrListener {
    override fun releaseEditor(previewState: PreviewState) {

    }

    override fun autoRefreshPreview(virtualFile: VirtualFile) {

    }

    override fun print(msg: String, contentType: ConsoleViewContentType) {

    }

    override fun activeConsoleView() {

    }

    override fun grammarChange(newFile: VirtualFile) {

    }

    override fun grammarSaved(virtualFile: VirtualFile) {

    }

    override fun startRuleName(virtualFile: VirtualFile, ruleName: String) {

    }

    override fun updateParseTreeState(virtualFile: VirtualFile) {

    }

    override fun clearParseErrors() {

    }

    override fun startParsing() {

    }

    override fun parsingCompleted(previewState: PreviewState, ms: Long) {

    }

    override fun parsingSlow() {

    }

    override fun cancelParsing() {

    }
}
