package com.antlr.language.psrsing

import com.intellij.openapi.progress.ProgressManager
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.*
import org.antlr.v4.tool.Grammar
import org.antlr.v4.tool.GrammarParserInterpreter

open class PreviewParser(g: Grammar, atn: ATN, input: TokenStream) : GrammarParserInterpreter(g, atn, input) {
    /** Map each preview editor token to the grammar ATN state used to match it.
     * Saves us having to create special token subclass and token factory.
     */
    var inputTokenToStateMap: MutableMap<Token?, Int?>? = HashMap<Token?, Int?>()

    private val lexerWatchdog: LexerWatchdog = LexerWatchdog(input, this)

    protected var lastSuccessfulMatchState: Int = ATNState.INVALID_STATE_NUMBER // not sure about error nodes

    constructor(g: Grammar, input: TokenStream) : this(
        g,
        ATNDeserializer().deserialize(ATNSerializer.getSerialized(g.getATN()).toArray()),
        input
    )

    override fun reset() {
        super.reset()
        if (inputTokenToStateMap != null) inputTokenToStateMap!!.clear()
        lastSuccessfulMatchState = ATNState.INVALID_STATE_NUMBER
    }

    override fun createInterpreterRuleContext(
        parent: ParserRuleContext?,
        invokingStateNumber: Int,
        ruleIndex: Int
    ): InterpreterRuleContext {
        return PreviewInterpreterRuleContext(parent, invokingStateNumber, ruleIndex)
    }

    override fun visitDecisionState(p: DecisionState): Int {
        ProgressManager.checkCanceled()

        val predictedAlt = super.visitDecisionState(p)
        if (p.numberOfTransitions > 1) {
            if (p.decision == this.overrideDecision &&
                this._input.index() == this.overrideDecisionInputIndex
            ) {
                (overrideDecisionRoot as PreviewInterpreterRuleContext).isDecisionOverrideRoot = true
            }
        }
        return predictedAlt
    }


    @Throws(RecognitionException::class)
    override fun match(ttype: Int): Token? {
        lexerWatchdog.checkLexerIsNotStuck()

        val t = super.match(ttype)
        // track which ATN state matches each token
        inputTokenToStateMap!![t] = state
        lastSuccessfulMatchState = state
        return t
    }


    @Throws(RecognitionException::class)
    override fun matchWildcard(): Token? {
        lexerWatchdog.checkLexerIsNotStuck()

        inputTokenToStateMap!![_input.LT(1)] = state
        lastSuccessfulMatchState = state
        return super.matchWildcard()
    }
}
