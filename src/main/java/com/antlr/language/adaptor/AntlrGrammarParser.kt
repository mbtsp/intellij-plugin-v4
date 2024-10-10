package com.antlr.language.adaptor

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrLanguage
import com.antlr.language.AntlrTokenTypes
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTree

class AntlrGrammarParser : ANTLRParserAdaptor(AntlrLanguage.INSTANCE, ANTLRv4Parser(null)) {
    override fun parse(parser: Parser, root: IElementType?): ParseTree {
        val startRule: Int = if (root is IFileElementType) {
            ANTLRv4Parser.RULE_grammarSpec
        } else if (root === AntlrTokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.TOKEN_REF]
            || root === AntlrTokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.RULE_REF]
        ) {
            ANTLRv4Parser.RULE_atom
        } else {
            Token.INVALID_TYPE
        }

        return when (startRule) {
            ANTLRv4Parser.RULE_grammarSpec -> (parser as ANTLRv4Parser).grammarSpec()

            ANTLRv4Parser.RULE_atom -> (parser as ANTLRv4Parser).atom()

            else -> throw UnsupportedOperationException(
                String.format(
                    "cannot start parsing using root element %s",
                    root
                )
            )
        }
    }
}
