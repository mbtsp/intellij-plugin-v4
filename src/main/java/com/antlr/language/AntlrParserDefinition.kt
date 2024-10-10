package com.antlr.language

import com.antlr.language.adaptor.AntlrGrammarParser
import com.antlr.language.adaptor.AntlrToolLexerAdaptor
import com.antlr.language.psi.AntlrASTFactory
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory

class AntlrParserDefinition : ParserDefinition {
    fun ANTLRv4ParserDefinition() {
        PSIElementTypeFactory.defineLanguageIElementTypes(
            AntlrLanguage.INSTANCE,
            ANTLRv4Lexer.VOCABULARY,
            ANTLRv4Parser.ruleNames, true
        )
    }

    override fun createLexer(project: Project?): Lexer {
        val lexer = ANTLRv4Lexer(null)
        return AntlrToolLexerAdaptor(lexer)
    }

    override fun createParser(project: Project?): PsiParser {
        return AntlrGrammarParser()
    }

    override fun getWhitespaceTokens(): TokenSet {
        return AntlrTokenTypes.WHITESPACES
    }

    override fun getCommentTokens(): TokenSet {
        return AntlrTokenTypes.COMMENTS
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return AntlrFileRoot(viewProvider)
    }

    override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements {
        return ParserDefinition.SpaceRequirements.MAY
    }

    /** Convert from internal parse node (AST they call it) to final PSI node. This
     * converts only internal rule nodes apparently, not leaf nodes. Leaves
     * are just tokens I guess.
     */
    override fun createElement(node: ASTNode?): PsiElement {
        return AntlrASTFactory.createInternalParseTreeNode(node!!)
    }

    companion object {
        val FILE: IFileElementType = IFileElementType(AntlrLanguage.INSTANCE)
    }
}
