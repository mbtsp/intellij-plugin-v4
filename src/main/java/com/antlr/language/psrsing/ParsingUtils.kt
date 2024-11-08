package com.antlr.language.psrsing

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.ANTLRv4Parser
import com.antlr.listener.AntlrListener
import com.antlr.preview.PreviewState
import com.antlr.setting.configdialogs.AntlrGrammarProperties
import com.antlr.setting.configdialogs.AntlrToolGrammarPropertiesStore.Companion.getGrammarProperties
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import org.antlr.intellij.adaptor.parser.SyntaxErrorListener
import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.RecognitionException
import org.antlr.v4.Tool
import org.antlr.v4.parse.ANTLRParser
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.PredictionMode
import org.antlr.v4.runtime.misc.Pair
import org.antlr.v4.runtime.misc.Predicate
import org.antlr.v4.runtime.misc.Utils
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.Tree
import org.antlr.v4.runtime.tree.Trees
import org.antlr.v4.tool.ErrorType
import org.antlr.v4.tool.Grammar
import org.antlr.v4.tool.LexerGrammar
import org.antlr.v4.tool.ast.GrammarRootAST
import java.io.File
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

object ParsingUtils {
    private val LOG = Logger.getInstance(ParsingUtils::class.java)
    private var BAD_PARSER_GRAMMAR: Grammar? = null
    private var BAD_LEXER_GRAMMAR: LexerGrammar? = null

    init {
        try {
            BAD_PARSER_GRAMMAR = Grammar("grammar BAD; a : 'bad' ;")
            BAD_PARSER_GRAMMAR!!.name = "BAD_PARSER_GRAMMAR"
            BAD_LEXER_GRAMMAR = LexerGrammar("lexer grammar BADLEXER; A : 'bad' ;")
            BAD_LEXER_GRAMMAR!!.name = "BAD_LEXER_GRAMMAR"
        } catch (re: RecognitionException) {
            LOG.error("can't init bad grammar markers")
        }
    }

    fun nextRealToken(tokens: CommonTokenStream, i: Int): Token? {
        var i:Int = i
        val n = tokens.size()
        i++ // search after current i token
        if (i >= n || i < 0) return null
        var t = tokens.get(i)
        while (t.channel != Token.DEFAULT_CHANNEL) {  // Parser must parse tokens on DEFAULT_CHANNEL
            if (t.type == Token.EOF) {
                val tokenSource = tokens.tokenSource ?: return CommonToken(Token.EOF, "EOF")
                val tokenFactory = tokenSource.tokenFactory ?: return CommonToken(Token.EOF, "EOF")
                return tokenFactory.create(Token.EOF, "EOF")
            }
            i++
            if (i >= n) return null // just in case no EOF

            t = tokens.get(i)
        }
        return t
    }

    fun previousRealToken(tokens: CommonTokenStream, i: Int): Token? {
        var i = i
        val size = tokens.size()
        i-- // search before current i token
        if (i >= size || i < 0) return null
        var t = tokens.get(i)
        while (t.channel != Token.DEFAULT_CHANNEL) { // Parser must parse tokens on DEFAULT_CHANNEL
            i--
            if (i < 0) return null
            t = tokens.get(i)
        }
        return t
    }

    fun getTokenUnderCursor(previewState: PreviewState?, offset: Int): Token? {
        if (previewState?.parsingResult == null) return null

        val parser = previewState.parsingResult!!.parser as PreviewParser
        val tokenStream = parser.inputStream as CommonTokenStream
        return getTokenUnderCursor(tokenStream, offset)
    }

    fun getTokenUnderCursor(tokens: CommonTokenStream, offset: Int): Token? {
        val cmp = Comparator { a: Token?, b: Token? ->
            if (a!!.stopIndex < b!!.startIndex) return@Comparator -1
            if (a.startIndex > b.stopIndex) return@Comparator 1
            0
        }
        if (offset < 0 || offset >= tokens.tokenSource.inputStream.size()) return null
        val key = CommonToken(Token.INVALID_TYPE, "")
        key.startIndex = offset
        key.stopIndex = offset
        val tokenList = tokens.tokens
        var tokenUnderCursor: Token? = null
        val i = Collections.binarySearch(tokenList, key, cmp)
        if (i >= 0) tokenUnderCursor = tokenList[i]
        return tokenUnderCursor
    }

    /*
    [77] = {org.antlr.v4.runtime.CommonToken@16710}"[@77,263:268='import',<25>,9:0]"
    [78] = {org.antlr.v4.runtime.CommonToken@16709}"[@78,270:273='java',<100>,9:7]"
     */
    fun getSkippedTokenUnderCursor(tokens: CommonTokenStream, offset: Int): Token? {
        if (offset < 0 || offset >= tokens.tokenSource.inputStream.size()) return null
        var prevToken: Token? = null
        var tokenUnderCursor: Token? = null
        for (t in tokens.tokens) {
            val begin = t.startIndex
            val end = t.stopIndex
            if ((prevToken == null || offset > prevToken.stopIndex) && offset < begin) {
                // found in between
                val tokenSource = tokens.tokenSource
                var inputStream: CharStream? = null
                if (tokenSource != null) {
                    inputStream = tokenSource.inputStream
                }
                tokenUnderCursor = CommonToken(
                    Pair(tokenSource, inputStream),
                    Token.INVALID_TYPE,
                    -1,
                    if (prevToken != null) prevToken.stopIndex + 1 else 0,
                    begin - 1
                )
                break
            }
            if (offset in begin..end) {
                tokenUnderCursor = t
                break
            }
            prevToken = t
        }
        return tokenUnderCursor
    }

    fun tokenizeANTLRGrammar(text: String): CommonTokenStream {
        val input = CharStreams.fromString(text)
        val lexer = ANTLRv4Lexer(input)
        val tokens: CommonTokenStream = TokenStreamSubset(lexer)
        tokens.fill()
        return tokens
    }

    fun getParseTreeNodeWithToken(tree: ParseTree?, token: Token?): ParseTree? {
        if (tree == null || token == null) {
            return null
        }

        val tokenNodes = Trees.findAllTokenNodes(tree, token.type)
        for (t in tokenNodes) {
            val tnode = t as TerminalNode
            if (tnode.payload === token) {
                return tnode
            }
        }
        return null
    }

    fun parseANTLRGrammar(text: String): ParsingResult {
        val input = CharStreams.fromString(text)
        val lexer = ANTLRv4Lexer(input)
        val tokens: CommonTokenStream = TokenStreamSubset(lexer)
        val parser = ANTLRv4Parser(tokens)

        val listener = SyntaxErrorListener()
        parser.removeErrorListeners()
        parser.addErrorListener(listener)
        lexer.removeErrorListeners()
        lexer.addErrorListener(listener)

        val t: ParseTree = parser.grammarSpec()
        return ParsingResult(parser, t, listener)
    }

    fun parseText(
        g: Grammar?,
        lg: LexerGrammar?,
        startRuleName: String?,
        grammarFile: VirtualFile?,
        inputText: String,
        project: Project
    ): ParsingResult? {
        if (g == null || lg == null) {
            LOG.info(
                "parseText can't parse: missing lexer or parser no Grammar object for " +
                        (grammarFile?.name ?: "<unknown file>")
            )
            return null
        }

        val grammarProperties = getGrammarProperties(project, grammarFile!!)
        val input: CharStream? = grammarProperties?.caseChangingStrategy
            ?.applyTo(CharStreams.fromString(inputText, grammarFile.path))
        val lexEngine: LexerInterpreter = lg.createLexerInterpreter(input)
        val syntaxErrorListener = SyntaxErrorListener()
        lexEngine.removeErrorListeners()
        lexEngine.addErrorListener(syntaxErrorListener)
        val tokens: CommonTokenStream = TokenStreamSubset(lexEngine)
        return parseText(g, lg, startRuleName, syntaxErrorListener, tokens, 0)
    }

    private fun parseText(
        g: Grammar,
        lg: LexerGrammar?,
        startRuleName: String?,
        syntaxErrorListener: SyntaxErrorListener?,
        tokens: TokenStream,
        startIndex: Int
    ): ParsingResult? {
        val grammarFileName = g.fileName
        if (!File(grammarFileName).exists()) {
            LOG.info("parseText grammar doesn't exist $grammarFileName")
            return null
        }

        if (g === BAD_PARSER_GRAMMAR || lg === BAD_LEXER_GRAMMAR) {
            return null
        }

        tokens.seek(startIndex)

        val parser = PreviewParser(g, tokens)
        parser.interpreter.predictionMode = PredictionMode.LL_EXACT_AMBIG_DETECTION
        parser.setProfile(true)

        parser.removeErrorListeners()
        parser.addErrorListener(syntaxErrorListener)

        val start = g.getRule(startRuleName)
            ?: return null // can't find start rule
        val t: ParseTree? = parser.parse(start.index)

        if (t != null) {
            return ParsingResult(parser, t, syntaxErrorListener)
        }
        return null
    }

    @JvmStatic
    fun createANTLRToolForLoadingGrammars(grammarProperties: AntlrGrammarProperties?): Tool {
        val antlr = Tool()
        antlr.errMgr = PluginIgnoreMissingTokensFileErrorManager(antlr)
        antlr.errMgr.setFormat("antlr")
        val listener = LoadGrammarsToolListener(antlr)
        antlr.removeListeners()
        antlr.addListener(listener)
        if (grammarProperties != null) {
            antlr.libDirectory = grammarProperties.libDir
        }
        return antlr
    }

    /**
     * Get lexer and parser grammars
     */
    fun loadGrammars(grammarFile: VirtualFile, project: Project): Array<Grammar?>? {
        if (project.isDisposed) {
            return null
        }
        LOG.info("loadGrammars " + grammarFile.path + " " + project.name)
        val antlr = createANTLRToolForLoadingGrammars(getGrammarProperties(project, grammarFile))
        val listener = antlr.listeners[0] as LoadGrammarsToolListener

        val g = loadGrammar(grammarFile, antlr)
        if (g == null) {
            reportBadGrammar(grammarFile, project)
            return null
        }

        // see if a lexer is hanging around somewhere; don't want implicit token defs to make us bail
        var lg: LexerGrammar? = null
        if (g.type == ANTLRParser.PARSER) {
            lg = loadLexerGrammarFor(g, project)
            if (lg != null) {
                g.importVocab(lg)
            } else {
                lg = BAD_LEXER_GRAMMAR
            }
        }

        antlr.process(g, false)
        if (listener.grammarErrorMessages.isNotEmpty()) {
            val msg = Utils.join(listener.grammarErrorMessages.iterator(), "\n")
            project.messageBus.syncPublisher(AntlrListener.TOPIC).print(msg + "\n", ConsoleViewContentType.ERROR_OUTPUT)
            return null // upon error, bail
        }

        // Examine's Grammar AST constructed by v3 for a v4 grammar.
        // Use ANTLR v3's ANTLRParser not ANTLRv4Parser from this plugin
        when (g.type) {
            ANTLRParser.PARSER -> {
                LOG.info("loadGrammars parser " + g.name)
                return arrayOf(lg, g)
            }

            ANTLRParser.LEXER -> {
                LOG.info("loadGrammars lexer " + g.name)
                lg = g as LexerGrammar
                return arrayOf(lg, null)
            }

            ANTLRParser.COMBINED -> {
                lg = g.getImplicitLexer()
                if (lg == null) {
                    lg = BAD_LEXER_GRAMMAR
                }
                LOG.info("loadGrammars combined: " + lg!!.name + ", " + g.name)
                return arrayOf(lg, g)
            }
        }
        LOG.info("loadGrammars invalid grammar type " + g.typeString + " for " + g.name)
        return null
    }

    private fun reportBadGrammar(grammarFile: VirtualFile, project: Project) {
        val msg = "Empty or bad grammar in file " + grammarFile.name
        project.messageBus.syncPublisher(AntlrListener.TOPIC).print(msg + "\n", ConsoleViewContentType.ERROR_OUTPUT)
    }


    private fun loadGrammar(grammarFile: VirtualFile, antlr: Tool): Grammar? {
        // basically here I am mimicking the loadGrammar() method from Tool
        // so that I can check for an empty AST coming back.
        val grammarRootAST = parseGrammar(antlr, grammarFile) ?: return null

        // Create a grammar from the AST so we can figure out what type it is
        val g = antlr.createGrammar(grammarRootAST)
        g.fileName = grammarFile.path

        return g
    }

    private fun parseGrammar(antlr: Tool, grammarFile: VirtualFile): GrammarRootAST? {
        val atomicReference = AtomicReference<GrammarRootAST?>(null)
        val countDownLatch = CountDownLatch(1)
        val documentAtomicReference = AtomicReference<Document?>()
        ApplicationManager.getApplication().runReadAction {
            try {
                val document = FileDocumentManager.getInstance().getDocument(grammarFile)
                documentAtomicReference.set(document)
            } catch (e: Exception) {
                documentAtomicReference.set(null)
            }
        }
        if (documentAtomicReference.get() == null) {
            return atomicReference.get()
        }

        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val grammarText =
                    if (documentAtomicReference.get() != null) documentAtomicReference.get()!!.text else String(
                        grammarFile.contentsToByteArray()
                    )
                val `in` = ANTLRStringStream(grammarText)
                `in`.name = grammarFile.path
                atomicReference.set(antlr.parse(grammarFile.path, `in`))
            } catch (e: Exception) {
                antlr.errMgr.toolError(ErrorType.CANNOT_OPEN_FILE, e, grammarFile)
            } finally {
                countDownLatch.countDown()
            }
        }
        try {
            countDownLatch.await()
        } catch (_: InterruptedException) {
        }
        return atomicReference.get()
    }

    /**
     * Try to load a LexerGrammar given a parser grammar g. Derive lexer name
     * as:
     * V given tokenVocab=V in grammar or
     * XLexer given XParser.g4 filename or
     * XLexer given grammar name X
     */
    private fun loadLexerGrammarFor(g: Grammar, project: Project): LexerGrammar? {
        val antlr = createANTLRToolForLoadingGrammars(getGrammarProperties(project, g.fileName))
        val listener = antlr.listeners[0] as LoadGrammarsToolListener
        var lg: LexerGrammar? = null
        val lexerGrammarFile: VirtualFile?

        val vocabName = g.getOptionString("tokenVocab")
        if (vocabName != null) {
            val grammarFile = LocalFileSystem.getInstance().findFileByIoFile(File(g.fileName))
            lexerGrammarFile =
                VfsUtil.findRelativeFile(grammarFile?.parent, "$vocabName.g4")
        } else {
            lexerGrammarFile =
                LocalFileSystem.getInstance().findFileByIoFile(File(getLexerNameFromParserFileName(g.fileName)))
        }

        if (lexerGrammarFile != null && lexerGrammarFile.exists()) {
            try {
                val grammar = loadGrammar(lexerGrammarFile, antlr)
                if (grammar is LexerGrammar) {
                    lg = grammar
                }
                if (lg != null) {
                    antlr.process(lg, false)
                } else {
                    reportBadGrammar(lexerGrammarFile, project)
                }
            } catch (cce: ClassCastException) {
                LOG.error("File $lexerGrammarFile isn't a lexer grammar", cce)
            } catch (e: Exception) {
                var msg: String? = null
                if (listener.grammarErrorMessages.isNotEmpty()) {
                    msg = ": " + listener.grammarErrorMessages
                }
                LOG.error("File $lexerGrammarFile couldn't be parsed as a lexer grammar$msg", e)
            }
            if (listener.grammarErrorMessages.isNotEmpty()) {
                lg = null
                val msg = Utils.join(listener.grammarErrorMessages.iterator(), "\n")
                project.messageBus.syncPublisher(AntlrListener.TOPIC)
                    .print(msg + "\n", ConsoleViewContentType.ERROR_OUTPUT)
            }
        }
        return lg
    }

    @JvmStatic
    fun getLexerNameFromParserFileName(parserFileName: String): String {
        val lexerGrammarFileName: String
        val i = parserFileName.indexOf("Parser.g4")
        if (i >= 0) { // is filename XParser.g4?
            lexerGrammarFileName = parserFileName.substring(0, i) + "Lexer.g4"
        } else { // if not, try using the grammar name, XLexer.g4
            val f = File(parserFileName)
            val name = f.getName()
            val dot = name.lastIndexOf(".g4")
            val parserName = name.substring(0, dot)
            val parentDir = f.getParentFile()
            lexerGrammarFileName = File(parentDir, parserName + "Lexer.g4").absolutePath
        }
        return lexerGrammarFileName
    }

    fun findOverriddenDecisionRoot(ctx: Tree?): Tree? {
        return Trees.findNodeSuchThat(
            ctx
        ) { t: Tree? -> t is PreviewInterpreterRuleContext && t.isDecisionOverrideRoot() }
    }

    fun getAllLeaves(t: Tree): MutableList<TerminalNode?> {
        val leaves: MutableList<TerminalNode?> = ArrayList<TerminalNode?>()
        _getAllLeaves(t, leaves)
        return leaves
    }

    private fun _getAllLeaves(t: Tree, leaves: MutableList<TerminalNode?>) {
        val n = t.childCount
        if (t is TerminalNode) {
            val tok = t.symbol
            if (tok.type != Token.INVALID_TYPE) {
                leaves.add(t)
            }
            return
        }
        for(i in 0 until n) {
            _getAllLeaves(t.getChild(i), leaves)
        }
    }

    /**
     * Get ancestors where the first element of the list is the parent of t
     */
    fun getAncestors(t: Tree?): MutableList<out Tree?> {
        if (t!!.parent == null) return mutableListOf()
        val ancestors: MutableList<Tree?> = ArrayList<Tree?>()
        var temp = t.parent
        while (temp != null) {
            ancestors.add(temp) // insert at start
            temp = temp.parent
        }
        return ancestors
    }
}
