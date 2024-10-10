package com.antlr.setting.configdialogs

import com.antlr.language.AntlrSyntaxHighlighter
import com.antlr.util.Icons
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class AntlrColorsPage : ColorSettingsPage {
    override fun getIcon(): Icon? {
        return Icons.FILE
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return AntlrSyntaxHighlighter()
    }

    override fun getDemoText(): String {
        return "grammar Foo;\n" +
                "\n" +
                "compilationUnit : STUFF EOF;\n" +
                "\n" +
                "STUFF : 'stuff' -> pushMode(OTHER_MODE);\n" +
                "WS : [ \\t]+ -> channel(HIDDEN);\n" +
                "NEWLINE : [\\r\\n]+ -> type(WS);\n" +
                "BAD_CHAR : . -> skip;\n" +
                "\n" +
                "mode OTHER_MODE;\n" +
                "\n" +
                "KEYWORD : 'keyword' -> popMode;\n"
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String?, TextAttributesKey?>? {
        return null
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor?> {
        return ATTRIBUTES
    }

    override fun getColorDescriptors(): Array<ColorDescriptor?> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getDisplayName(): String {
        return "ANTLR"
    }

    companion object {
        private val ATTRIBUTES = arrayOf<AttributesDescriptor?>(
            AttributesDescriptor("Lexer Rule", AntlrSyntaxHighlighter.TOKEN_NAME),
            AttributesDescriptor("Parser Rule", AntlrSyntaxHighlighter.RULE_NAME),
        )
    }
}
