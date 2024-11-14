package com.antlr.service

import com.antlr.language.psrsing.ParsingUtils
import com.antlr.language.psrsing.RunAntlrOnGrammarFile
import com.antlr.listener.AntlrListener
import com.antlr.preview.PreviewState
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.util.BackgroundTaskUtil
import com.intellij.openapi.progress.util.ProgressWindow
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import org.antlr.v4.parse.ANTLRParser
import org.antlr.v4.tool.LexerGrammar


@Service(Service.Level.PROJECT)
class AntlrService(private val project: Project) {
    val logger = Logger.getInstance(AntlrService::class.java)
    private val grammarToPreviewState = mutableMapOf<String, PreviewState>()
    private val grammarFileMods = mutableMapOf<String, Long>()
    private var progressIndicator: ProgressIndicator? = null

    fun updatePreViewState(virtualFile: VirtualFile, previewState: PreviewState) {
        grammarToPreviewState[virtualFile.path] = previewState
    }

    fun previewState(virtualFile: VirtualFile): PreviewState {
        if (!grammarToPreviewState.containsKey(virtualFile.path)) {
            val previewState = PreviewState(project, virtualFile)
            grammarToPreviewState[virtualFile.path] = previewState
            return previewState
        }
        return grammarToPreviewState[virtualFile.path]!!
    }

    fun parseText(virtualFile: VirtualFile, inputText: String?): ProgressIndicator? {
        if (!this.project.isDisposed) {
            this.project.messageBus.syncPublisher(AntlrListener.TOPIC).clearParseErrors()
            this.project.messageBus.syncPublisher(AntlrListener.TOPIC).startParsing()
        }
        val previewState = previewState(virtualFile)
        if (inputText.isNullOrBlank()) {
            return null
        }
        this.progressIndicator = BackgroundTaskUtil.executeAndTryWait(
            { _: ProgressIndicator ->
                val start = System.nanoTime()
                previewState.parsingResult = ParsingUtils.parseText(
                    previewState.g, previewState.lg, previewState.startRuleName,
                    virtualFile, inputText, project
                )
                Runnable {
                    if (!project.isDisposed) {
                        project.messageBus.syncPublisher(AntlrListener.TOPIC)
                            .parsingCompleted(previewState, System.nanoTime() - start)
                    }
                }
            },
            {
                if (!project.isDisposed) {
                    project.messageBus.syncPublisher(AntlrListener.TOPIC).parsingSlow()
                }
            },
            ProgressWindow.DEFAULT_PROGRESS_DIALOG_POSTPONE_TIME_MILLIS.toLong(),
            false
        )
        return progressIndicator
    }

    fun cancelParsing() {
        progressIndicator?.cancel()
        if (!this.project.isDisposed) {
            this.project.messageBus.syncPublisher(AntlrListener.TOPIC).cancelParsing()
        }
    }

    fun updateGrammar(virtualFile: VirtualFile, generateTokensFile: Boolean) {
        if (project.isDisposed) {
            return
        }
        updateGrammar(virtualFile)
        val previewState = getAssociatedParserIfLexer(virtualFile.path)
        if (previewState != null) {
            if (generateTokensFile) {
                runAntlrTool(virtualFile)
            }
            updateGrammar(previewState.grammarFile)
        }
    }

    private fun runAntlrTool(virtualFile: VirtualFile) {
        val gen = RunAntlrOnGrammarFile(virtualFile, project, "Antlr Code Generation", true, false)
        gen.queue()
    }

    private fun updateGrammar(virtualFile: VirtualFile) {
        if (project.isDisposed) {
            return
        }
        val task = object : Task.Backgroundable(project, "Update Grammar for ${virtualFile.path}", false) {
            override fun run(indicator: ProgressIndicator) {
                val previewState = previewState(virtualFile)
                val grammars = ParsingUtils.loadGrammars(virtualFile, this.project)?.let {
                    synchronized(previewState) {
                        previewState.lg = it[0] as LexerGrammar
                        previewState.g = it[1]

                    }
                    updatePreViewState(virtualFile, previewState)
                }
                if (grammars == null) {
                    previewState.g = null
                    previewState.lg = null
                    updatePreViewState(virtualFile, previewState)
                }


            }
        }
        task.queue()
    }

    fun saveGrammar(virtualFile: VirtualFile) {
        val modCount = virtualFile.modificationCount
        if (grammarFileMods.containsKey(virtualFile.path) && grammarFileMods[virtualFile.path]?.equals(modCount) == true) {
            return
        }
        grammarFileMods[virtualFile.path] = modCount
        updateGrammar(virtualFile, true)
        if (!project.isDisposed) {
            project.messageBus.syncPublisher(AntlrListener.TOPIC).grammarSaved(virtualFile)
        }
    }


    private fun getAssociatedParserIfLexer(fileName: String): PreviewState? {
        for (pair in grammarToPreviewState.values) {
            if (pair.lg != null && (sameFile(
                    fileName,
                    pair.lg!!.fileName
                ) || pair.lg == ParsingUtils.BAD_PARSER_GRAMMAR)
            ) {
                if (pair.g != null && pair.g!!.type == ANTLRParser.PARSER) {
                    return pair
                }
            }
            if (pair.g != null && pair.g!!.importedGrammars != null) {
                for (importGrammar in pair.g!!.importedGrammars) {
                    if (importGrammar.fileName.equals(fileName)) {
                        return pair
                    }
                }
            }
        }
        return null
    }

    private fun sameFile(onePath: String, towPath: String): Boolean {
        return FileUtil.comparePaths(onePath, towPath) == 0
    }
}
