package com.antlr.language

import com.intellij.lang.Language


class AntlrFileLanguage: Language("Antlr-Tool") {
    companion object {
        val INSTANCE = AntlrFileLanguage()
    }
}
