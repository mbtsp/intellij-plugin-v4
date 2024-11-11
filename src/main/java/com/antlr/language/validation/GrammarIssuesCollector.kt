package com.antlr.language.validation

import com.antlr.util.AntlrUtil.getArgsAsList
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiFile
import org.antlr.runtime.ANTLRReaderStream
import org.antlr.v4.Tool
import org.antlr.v4.codegen.CodeGenerator
import org.antlr.v4.codegen.Target
import org.antlr.v4.parse.ANTLRParser
import org.antlr.v4.runtime.misc.IntervalSet
import org.antlr.v4.tool.*
import org.antlr.v4.tool.ast.GrammarAST
import org.antlr.v4.tool.ast.RuleRefAST
import org.stringtemplate.v4.ST
import java.io.File
import java.io.StringReader

object GrammarIssuesCollector {
    private const val LANGUAGE_ARG_PREFIX = "-Dlanguage="

    private val LOG: Logger = Logger.getInstance(
        GrammarIssuesCollector::class.java.name
    )

    fun collectGrammarIssues(file: PsiFile): List<GrammarIssue> {
        val grammarFileName = file.virtualFile.path
        LOG.info("doAnnotate $grammarFileName")
        val fileContents = file.text
        val args = getArgsAsList(file.project, file.virtualFile)
        val listener = GrammarIssuesCollectorToolListener()

        val languageArg = findLanguageArg(args)

        if (languageArg != null) {
            val language = languageArg.substring(LANGUAGE_ARG_PREFIX.length)

            if (!targetExists(language)) {
                val issue = GrammarIssue(null)
                issue.annotation =
                    "Unknown target language '$language', analysis will be done using the default target language 'Java'"
                listener.issues.add(issue)

                args.remove(languageArg)
            }
        }

        val antlr = Tool(args.toTypedArray<String>())
        if (!args.contains("-lib")) {
            // getContainingDirectory() must be identified as a read operation on a file system
            ApplicationManager.getApplication().runReadAction {
                antlr.libDirectory = file.containingDirectory.toString()
            }
        }

        antlr.removeListeners()
        antlr.addListener(listener)
        try {
            val sr = StringReader(fileContents)
            val `in` = ANTLRReaderStream(sr)
            `in`.name = file.name
            val ast = antlr.parse(file.name, `in`)
            if (ast == null || ast.hasErrors) {
                for (issue in listener.issues) {
                    processIssue(file, issue)
                }
                return listener.issues
            }
            val g = antlr.createGrammar(ast)
            g.fileName = grammarFileName

            val vocabName = g.getOptionString("tokenVocab")
            if (vocabName != null) { // import vocab to avoid spurious warnings
                LOG.info("token vocab file $vocabName")
                g.importTokensFromTokensFile()
            }

            val vfile = file.virtualFile
            if (vfile == null) {
                LOG.error("doAnnotate no virtual file for $file")
                return listener.issues
            }
            g.fileName = vfile.path
            antlr.process(g, false)

            val unusedRules = getUnusedParserRules(g)
            if (unusedRules != null) {
                for (r in unusedRules.keys) {
                    val ruleDefToken = unusedRules[r]!!.getToken()
                    val issue = GrammarIssue(GrammarInfoMessage(g.fileName, ruleDefToken, r))
                    listener.issues.add(issue)
                }
            }

            for (issue in listener.issues) {
                processIssue(file, issue)
            }
        } catch (e: Exception) {
            LOG.error("antlr can't process " + file.name, e)
        }
        return listener.issues
    }

    private fun findLanguageArg(args: List<String>): String? {
        for (arg in args) {
            if (arg.startsWith(LANGUAGE_ARG_PREFIX)) {
                return arg
            }
        }

        return null
    }

    private fun processIssue(file: PsiFile, issue: GrammarIssue) {
        val grammarFile = File(file.virtualFile.path)
        if (issue.msg == null || issue.msg.fileName == null) { // weird, the issue doesn't have a file associated with it
            return
        }
        val issueFile = File(issue.msg.fileName)
        if (grammarFile.name != issueFile.name) {
            return  // ignore errors from external files
        }
        var msgST: ST? = null
        when (issue.msg) {
            is GrammarInfoMessage -> { // not in ANTLR so must hack it in
                val t = issue.msg.offendingToken
                issue.offendingTokens.add(t)
                msgST = ST("unused parser rule <arg>")
                msgST.add("arg", t.text)
                msgST.impl.name = "info"
            }

            is GrammarSemanticsMessage -> {
                val t = issue.msg.offendingToken
                issue.offendingTokens.add(t)
            }

            is LeftRecursionCyclesMessage -> {
                val rulesToHighlight: MutableList<String> = ArrayList()
                val cycles =
                    issue.msg.args[0] as Collection<MutableCollection<Rule>>
                for (cycle in cycles) {
                    for (r in cycle) {
                        rulesToHighlight.add(r.name)
                        val nameNode = r.ast.getChild(0) as GrammarAST
                        issue.offendingTokens.add(nameNode.getToken())
                    }
                }
            }

            is GrammarSyntaxMessage -> {
                val t = issue.msg.offendingToken
                issue.offendingTokens.add(t)
            }

            is ToolMessage -> {
                issue.offendingTokens.add(issue.msg.offendingToken)
            }
        }

        val antlr = Tool()
        if (msgST == null) {
            msgST = antlr.errMgr.getMessageTemplate(issue.msg)
        }
        var outputMsg = msgST!!.render()
        if (antlr.errMgr.formatWantsSingleLineMessage()) {
            outputMsg = outputMsg.replace('\n', ' ')
        }
        issue.annotation = outputMsg
    }

    private fun getUnusedParserRules(g: Grammar): Map<String, GrammarAST>? {
        if (g.ast == null || g.isLexer) return null
        val ruleNodes = g.ast.getNodesWithTypePreorderDFS(IntervalSet.of(ANTLRParser.RULE_REF))
        // in case of errors, we walk AST ourselves
        // ANTLR's Grammar object might have bailed on rule defs etc...
        val ruleRefs: MutableSet<String> = HashSet()
        val ruleDefs: MutableMap<String, GrammarAST> = HashMap()
        for (x in ruleNodes) {
            if (x.getParent().type == ANTLRParser.RULE) {
                ruleDefs[x.text] = x
            } else if (x is RuleRefAST) {
                ruleRefs.add(x.getText())
            }
        }
        ruleDefs.keys.removeAll(ruleRefs)
        return ruleDefs
    }

    private fun targetExists(language: String): Boolean {
        val targetName = "org.antlr.v4.codegen.target." + language + "Target"
        try {
            val c = Class.forName(targetName).asSubclass(
                Target::class.java
            )
            c.getConstructor(CodeGenerator::class.java)
            return true
        } catch (e: Exception) { // ignore errors; we're detecting presence only
        }
        return false
    }
}
