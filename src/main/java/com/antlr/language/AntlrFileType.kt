package com.antlr.language

import com.antlr.util.Icons
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class AntlrFileType : LanguageFileType(AntlrFileLanguage.INSTANCE) {
    companion object {
        val INSTANCE = AntlrFileType()
    }
    override fun getName(): @NonNls String {
        return "Antlr grammar file"
    }

    override fun getDescription(): @NlsContexts.Label String {
        return "Antlr grammar file"
    }

    override fun getDefaultExtension(): @NlsSafe String {
        return "g4"
    }

    override fun getIcon(): Icon {
        return Icons.FILE
    }
}
