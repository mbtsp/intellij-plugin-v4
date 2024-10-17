package com.antlr.setting.configdialogs

import com.antlr.language.psrsing.CaseChangingStrategy
import com.intellij.util.xmlb.Converter

class CaseChangingStrategyConverter : Converter<CaseChangingStrategy>() {
    override fun fromString(value: String): CaseChangingStrategy? {
        try {
            return CaseChangingStrategy.valueOf(value)
        } catch (e: IllegalArgumentException) {
            return null
        }
    }

    override fun toString(caseChangingStrategy: CaseChangingStrategy): String? {
        return caseChangingStrategy.name
    }
}
