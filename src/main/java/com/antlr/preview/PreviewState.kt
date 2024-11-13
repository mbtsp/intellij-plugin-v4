package com.antlr.preview

import com.antlr.language.psrsing.ParsingResult
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.antlr.v4.tool.Grammar
import org.antlr.v4.tool.LexerGrammar

/**
 * Track everything associated with the state of the preview window.
 * For each grammar, we need to track an InputPanel (with <= 2 editor objects)
 * that we will flip to every time we come back to a specific grammar,
 * uniquely identified by the fully-qualified grammar name.
 *
 *
 * Before parsing can begin, we need to know the start rule. That means that
 * we should not show an editor until this field is filled in.
 *
 *
 * The plug-in controller should update all of these elements atomically so
 * they are self consistent.  We must be careful then to send these fields
 * around together as a unit instead of asking the controller for the
 * elements piecemeal. That could get g and lg for different grammar files,
 * for example.
 */
class PreviewState(var project: Project, var grammarFile: VirtualFile) {
    var g: Grammar? = null
    var lg: LexerGrammar? = null
    var startRuleName: String? = null
    var manualInputText: CharSequence = "" // save input when switching grammars
    var inputFile: VirtualFile? = null // save input file when switching grammars

    @JvmField
    var parsingResult: ParsingResult? = null

    /**
     * The current input editor (inputEditor or fileEditor) for this grammar
     * in InputPanel. This can be null when a PreviewState and InputPanel
     * are created out of sync. Depends on order IDE opens files vs
     * creates preview pane.
     */
    private var inputEditor: Editor? = null

    @Synchronized
    fun getInputEditor(): Editor? {
        return inputEditor
    }

    @Synchronized
    fun setInputEditor(inputEditor: Editor?) {
        releaseEditor()
        this.inputEditor = inputEditor
    }

    val mainGrammar: Grammar?
        get() = if (g != null) g else lg

    @Synchronized
    fun releaseEditor() {
        // Editor can't be release during unit tests, because it is used by multiple tests

        if (ApplicationManager.getApplication().isUnitTestMode) return

        // It would appear that the project closed event occurs before these
        // close grammars sometimes. Very strange. check for null editor.
        if (inputEditor != null) {
            val factory = EditorFactory.getInstance()
            factory.releaseEditor(inputEditor!!)
            inputEditor = null
        }
    }
}
