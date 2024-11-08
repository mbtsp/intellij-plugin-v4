package com.antlr.language.adaptor

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.AntlrLanguage
import com.intellij.lang.Language
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor
import org.antlr.intellij.adaptor.lexer.ANTLRLexerState
import org.antlr.v4.runtime.Lexer

class AntlrToolLexerAdaptor(lexer:ANTLRv4Lexer)
/**
 * Constructs a new instance of [ANTLRLexerAdaptor] with
 * the specified [Language] and underlying ANTLR [ ].
 *
 * @param language The language.
 * @param lexer    The underlying ANTLR lexer.
 */
    : ANTLRLexerAdaptor(AntlrLanguage.INSTANCE, lexer){
    override fun getInitialState(): ANTLRLexerState? {
        return MyAntlrLexerState(Lexer.DEFAULT_TOKEN_CHANNEL,null,0)
    }

    override fun getLexerState(lexer: Lexer): ANTLRLexerState? {
        if(lexer._modeStack.isEmpty){
            return MyAntlrLexerState(lexer._mode,null,(lexer as ANTLRv4Lexer).currentRuleType)
        }
        return MyAntlrLexerState(lexer._mode,lexer._modeStack,(lexer as ANTLRv4Lexer).currentRuleType)
    }
}
