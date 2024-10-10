package com.antlr.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class AntlrFileRoot(viewProvider: FileViewProvider) : PsiFileBase(viewProvider,AntlrFileLanguage.INSTANCE) {
    override fun getFileType(): FileType {
        return AntlrFileType.INSTANCE
    }

    override fun toString(): String {
        return "Antlr  grammar file"
    }
}
