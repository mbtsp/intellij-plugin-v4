package com.antlr.language.validation

import org.antlr.v4.tool.ANTLRMessage
import org.antlr.v4.tool.ANTLRToolListener

class GrammarIssuesCollectorToolListener : ANTLRToolListener {
    @JvmField
    val issues: MutableList<GrammarIssue?> = ArrayList<GrammarIssue?>()

    override fun info(msg: String?) {
    }

    override fun error(msg: ANTLRMessage?) {
        issues.add(GrammarIssue(msg))
    }

    override fun warning(msg: ANTLRMessage?) {
        issues.add(GrammarIssue(msg))
    }
}
