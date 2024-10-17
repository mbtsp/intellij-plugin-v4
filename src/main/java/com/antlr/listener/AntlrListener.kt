package com.antlr.listener

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.Topic

interface AntlrListener {
    companion object {
        val TOPIC = Topic.create("AntlrListener", AntlrListener::class.java)
    }

    fun autoRefreshPreview(virtualFile: VirtualFile)

    fun print(msg: String, contentType: ConsoleViewContentType)
}
