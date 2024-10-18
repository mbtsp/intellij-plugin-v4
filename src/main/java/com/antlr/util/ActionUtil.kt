package com.antlr.util

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.vfs.VirtualFile

fun AnActionEvent.isAntlrFile(): Boolean {
    val virtualFiles = this.getData(LangDataKeys.VIRTUAL_FILE_ARRAY)
    if (virtualFiles.isNullOrEmpty()) return false
    if (virtualFiles[0].name.endsWith(".g4") == true) return true
    return false
}

fun AnActionEvent.getGrammarFile(): VirtualFile? {
    val virtualFiles = this.getData(LangDataKeys.VIRTUAL_FILE_ARRAY)
    if (virtualFiles.isNullOrEmpty()) return null
    if (virtualFiles[0].name.endsWith(".g4")) return virtualFiles[0]
    return null
}
