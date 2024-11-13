package com.antlr.listener

import com.antlr.preview.PreviewState
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.Topic

interface AntlrListener {
    companion object {
        val TOPIC = Topic.create("AntlrListener", AntlrListener::class.java)
    }

    fun releaseEditor(previewState: PreviewState)
    fun autoRefreshPreview(virtualFile: VirtualFile)

    fun print(msg: String, contentType: ConsoleViewContentType)

    fun activeConsoleView()

    fun grammarChange(newFile: VirtualFile)
    fun grammarSaved(virtualFile: VirtualFile)

    fun startRuleName(virtualFile: VirtualFile, ruleName: String)

    fun updateParseTreeState(virtualFile: VirtualFile)

    //parser g4 file listeners

    fun clearParseErrors()

    fun startParsing()

    fun parsingCompleted(previewState: PreviewState, ms: Long)

    fun parsingSlow()

    fun cancelParsing()


}
