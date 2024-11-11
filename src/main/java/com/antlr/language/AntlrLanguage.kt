package com.antlr.language

import com.intellij.lang.Language

open class AntlrLanguage : Language("Antlr-Tool") {
    companion object {
        val INSTANCE = AntlrLanguage()
    }
}
