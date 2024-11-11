package com.antlr.language.adaptor

import com.antlr.language.ANTLRv4Lexer
import org.antlr.intellij.adaptor.lexer.ANTLRLexerState
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.misc.IntegerStack
import org.antlr.v4.runtime.misc.MurmurHash

class MyAntlrLexerState(mode: Int, modeStack: IntegerStack?, currentRuleType: Int) : ANTLRLexerState(mode, modeStack) {
    var currentRuleType: Int

    init {
        this.currentRuleType = currentRuleType
    }

    override fun apply(lexer: Lexer) {
        super.apply(lexer)
        if (lexer is ANTLRv4Lexer) {
            lexer.currentRuleType = currentRuleType
        }
    }

    override fun hashCodeImpl(): Int {
        var hash = MurmurHash.initialize()
        hash = MurmurHash.update(hash, mode)
        hash = MurmurHash.update(hash, modeStack)
        hash = MurmurHash.update(hash, currentRuleType)
        return MurmurHash.finish(hash, 3)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is MyAntlrLexerState) {
            return false
        }
        if (!super.equals(obj)) {
            return false
        }
        return currentRuleType == obj.currentRuleType
    }

}
