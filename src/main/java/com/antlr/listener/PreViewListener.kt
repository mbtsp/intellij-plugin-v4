package com.antlr.listener

import com.antlr.preview.PreviewState
import com.antlr.ui.view.PreviewPanel
import com.intellij.openapi.vfs.VirtualFile

class PreViewListener(val previewPanel: PreviewPanel) : AbstractAntlrListener() {
    override fun releaseEditor(previewState: PreviewState) {
        previewPanel.getInputPanel().releaseEditor(previewState)
    }

    override fun startRuleName(virtualFile: VirtualFile, ruleName: String) {
        previewPanel.getInputPanel().setStartRuleName(virtualFile, ruleName)
    }

    override fun updateParseTreeState(virtualFile: VirtualFile) {
        previewPanel.updateParseTreeFromDoc(virtualFile)
    }

    override fun grammarChange(newFile: VirtualFile) {
        previewPanel.grammarFileChanged(newFile)
    }

    override fun grammarSaved(virtualFile: VirtualFile) {
        previewPanel.grammarFileSaved(virtualFile)
    }

    override fun parsingCompleted(previewState: PreviewState, ms: Long) {
        previewPanel.onParsingCompleted(previewState, ms)
    }

    override fun parsingSlow() {
        previewPanel.notifySlowParsing()
    }

    override fun cancelParsing() {
        previewPanel.onParsingCancelled()
    }

    override fun clearParseErrors() {
        previewPanel.inputPanel.clearParseErrors()
    }

    override fun startParsing() {
        previewPanel.startParsing()
    }

    override fun autoRefreshPreview(virtualFile: VirtualFile) {
        previewPanel.autoRefreshPreview(virtualFile)
    }
}
