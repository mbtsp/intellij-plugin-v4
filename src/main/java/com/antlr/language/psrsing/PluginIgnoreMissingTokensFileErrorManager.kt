package com.antlr.language.psrsing

import org.antlr.v4.Tool
import org.antlr.v4.tool.ANTLRMessage
import org.antlr.v4.tool.ErrorManager
import org.antlr.v4.tool.ErrorType

class PluginIgnoreMissingTokensFileErrorManager(tool: Tool?) : ErrorManager(tool) {
    override fun emit(type: ErrorType?, msg: ANTLRMessage?) {
        if (type == ErrorType.CANNOT_FIND_TOKENS_FILE_REFD_IN_GRAMMAR ||
            type == ErrorType.CANNOT_FIND_TOKENS_FILE_GIVEN_ON_CMDLINE
        ) {
            return  // ignore these
        }
        super.emit(type, msg)
    }
}
