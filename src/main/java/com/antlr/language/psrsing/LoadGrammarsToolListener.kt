package com.antlr.language.psrsing

import org.antlr.v4.Tool
import org.antlr.v4.tool.ANTLRMessage
import org.antlr.v4.tool.DefaultToolListener

class LoadGrammarsToolListener(tool: Tool?) : DefaultToolListener(tool) {
    @JvmField
    var grammarErrorMessages: MutableList<String> = ArrayList<String>()
    var grammarWarningMessages: MutableList<String> = ArrayList<String>()

    override fun error(msg: ANTLRMessage?) {
        val msgST = tool.errMgr.getMessageTemplate(msg)
        var s = msgST.render()
        if (tool.errMgr.formatWantsSingleLineMessage()) {
            s = s.replace('\n', ' ')
        }
        grammarErrorMessages.add(s)
    }

    override fun warning(msg: ANTLRMessage?) {
        val msgST = tool.errMgr.getMessageTemplate(msg)
        var s = msgST.render()
        if (tool.errMgr.formatWantsSingleLineMessage()) {
            s = s.replace('\n', ' ')
        }
        grammarWarningMessages.add(s)
    }

    fun clear() {
        grammarErrorMessages.clear()
        grammarWarningMessages.clear()
    }
}
