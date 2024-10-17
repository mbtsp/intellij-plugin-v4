package com.antlr.language.folding

abstract class AntlrFoldingSettings {

    abstract val isCollapseFileHeader: Boolean


    abstract val isCollapseDocComments: Boolean


    abstract val isCollapseComments: Boolean


    abstract val isCollapseRuleBlocks: Boolean


    abstract val isCollapseActions: Boolean


    abstract val isCollapseTokens: Boolean

    companion object {
        //TODO ServiceManager,UI,serialization,etc
        val instance: AntlrFoldingSettings = object : AntlrFoldingSettings() {
            override val isCollapseFileHeader: Boolean
                get() = false
            override val isCollapseDocComments: Boolean
                get() = false
            override val isCollapseComments: Boolean
                get() = false
            override val isCollapseRuleBlocks: Boolean
                get() = false
            override val isCollapseActions: Boolean
                get() = true
            override val isCollapseTokens: Boolean
                get() = true
        }
    }
}
