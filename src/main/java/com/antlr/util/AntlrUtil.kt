package com.antlr.util

import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrTokenTypes
import com.antlr.language.psi.*
import com.antlr.language.psrsing.ParsingUtils
import com.antlr.language.psrsing.RunAntlrListener
import com.antlr.listener.AntlrListener
import com.antlr.service.AntlrService
import com.antlr.setting.configdialogs.AntlrGrammarProperties
import com.antlr.setting.configdialogs.AntlrToolGrammarPropertiesStore
import com.antlr.ui.ProfilerPanel
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.stream
import org.antlr.v4.Tool
import org.antlr.v4.codegen.CodeGenerator
import org.antlr.v4.runtime.atn.DecisionEventInfo
import org.stringtemplate.v4.misc.Misc
import java.awt.Point
import java.awt.event.MouseEvent
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import java.util.regex.Pattern


fun AnActionEvent.isAntlrFile(): Boolean {
    val virtualFiles = this.getData(LangDataKeys.VIRTUAL_FILE_ARRAY)
    if (virtualFiles.isNullOrEmpty()) return false
    if (virtualFiles[0].name.endsWith(".g4")) return true
    return false
}

fun Project.getCurrentEditorFile(): VirtualFile? {
    val fileManager = FileEditorManager.getInstance(this)
    val files = fileManager.selectedFiles
    if (files.isEmpty()) return null
    return files[0]
}

fun Project.getCurrentEditorFileG4(): VirtualFile? {
    val fileManager = FileEditorManager.getInstance(this)
    val files = fileManager.selectedFiles
    if (files.isEmpty()) return null
    for (file in files) {
        if (file.name.endsWith(".g4")) {
            return file
        }
    }
    return null
}

fun AnActionEvent.getGrammarFile(): VirtualFile? {
    val virtualFiles = this.getData(LangDataKeys.VIRTUAL_FILE_ARRAY)
    if (virtualFiles.isNullOrEmpty()) return null
    if (virtualFiles[0].name.endsWith(".g4")) return virtualFiles[0]
    return null
}

fun Editor.getRangeHighlightersAtOffset(offset: Int): List<RangeHighlighter> {
    val highlighters = mutableListOf<RangeHighlighter>()
    for (highlighter in this.markupModel.allHighlighters) {
        if (offset >= highlighter.startOffset && offset < highlighter.endOffset) {
            highlighters.add(highlighter)
        }
    }
    return highlighters
}

fun getHighlighterWithDecisionEventType(
    highlighters: List<RangeHighlighter>,
    decisionEventType: Class<*>
): DecisionEventInfo? {
    for (r in highlighters) {
        val eventInfo: DecisionEventInfo? = r.getUserData(ProfilerPanel.DECISION_EVENT_INFO_KEY)
        if (eventInfo != null) {
            if (eventInfo::class.java === decisionEventType) {
                return eventInfo
            }
        }
    }
    return null
}


fun AnActionEvent.isGrammar() {
    val file = this.getGrammarFile()
    if (file == null) {
        this.presentation.isEnabled = false
        return
    }
    this.presentation.isEnabledAndVisible = true
}

fun VirtualFile.getEditor(project: Project): Editor? {
    val fileManger = FileDocumentManager.getInstance()
    val doc = fileManger.getDocument(this) ?: return null
    val factory = EditorFactory.getInstance()
    val editors = factory.getEditors(doc, project)
    if (editors.isEmpty()) return null
    return editors[0]
}

fun AnActionEvent.getParserRuleSurroundingRef(): ParserRuleRefNode? {
    val psiNode = this.getSelectedPsiElement() ?: return null
    val ruleSpecNode = psiNode.getRuleSurroundingRef(ParserRuleSpecNode::class.java) ?: return null
    return PsiTreeUtil.findChildOfType(ruleSpecNode, ParserRuleRefNode::class.java)

}

fun AnActionEvent.getLexerRuleSurroundingRef(): LexerRuleRefNode? {
    val psiNode = this.getSelectedPsiElement() ?: return null
    val ruleSpecNode = psiNode.getRuleSurroundingRef(LexerRuleSpecNode::class.java) ?: return null
    return PsiTreeUtil.findChildOfType(ruleSpecNode, LexerRuleRefNode::class.java)
}

fun AnActionEvent.getSelectedPsiElement(): PsiElement? {
    val editor = this.getData(PlatformDataKeys.EDITOR)
    if (editor == null) {
        val psiElement = this.getData(LangDataKeys.PSI_ELEMENT)
        if (psiElement == null || psiElement !is ParserRuleRefNode) {
            return null
        }
        return psiElement
    }
    val file = this.getData(LangDataKeys.PSI_FILE) ?: return null
    val offset = editor.caretModel.offset
    return file.findElementAt(offset)
}

fun PsiElement.getRuleSurroundingRef(type: Class<out RuleSpecNode>): RuleSpecNode? {
    if (this.javaClass != type) {
        val psiElement = PsiTreeUtil.findFirstParent(
            this
        ) { p0 -> p0.javaClass == type }
        if (psiElement != null) {
            return psiElement as RuleSpecNode
        }
    }
    return null
}


object AntlrUtil {
    private const val GROUP_DISPLAY_ID = "ANTLR Tool Parser Generation"
    private val log = Logger.getInstance(AntlrUtil::class.java)
    val PACKAGE_DEFINITION_REGEX: Pattern = Pattern.compile("package\\s+[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_];")

    fun getMouseOffset(mouseEvent: MouseEvent, editor: Editor): Int {
        val point = Point(mouseEvent.getPoint())
        val pos = editor.xyToLogicalPosition(point)
        return editor.logicalPositionToOffset(pos)
    }

    fun generateCode(project: Project, virtualFile: VirtualFile, forceGenerateCode: Boolean) {
        val antlrGrammarProperties = AntlrToolGrammarPropertiesStore.getGrammarProperties(project, virtualFile)
        var autoGen = false
        if (antlrGrammarProperties != null) {
            autoGen = antlrGrammarProperties.autoGen
        }
        if (forceGenerateCode || autoGen && isGrammarFile(project, virtualFile)) {
            ReadAction.run<RuntimeException> {
                antlr(project, virtualFile)
            }
            return
        }
        val previewState = AntlrService.getInstance(project).previewState(virtualFile)
        if (previewState.g == null && previewState.lg != null) {
            val grammar = previewState.lg
            val language = grammar?.getOptionString(AntlrGrammarProperties.PROP_LANGUAGE)
            val tool = ParsingUtils.createANTLRToolForLoadingGrammars(
                AntlrToolGrammarPropertiesStore.getGrammarProperties(
                    project,
                    virtualFile
                )
            )
            val gen = CodeGenerator.create(tool, grammar, language)
            gen.writeVocabFile()
        }
    }

    fun getEditor(project: Project, file: VirtualFile): Editor? {
        val fdm = FileDocumentManager.getInstance()
        val doc = fdm.getDocument(file) ?: return null

        val factory: EditorFactory = EditorFactory.getInstance()
        val editors: Array<Editor> = factory.getEditors(doc, project)
        if (editors.isEmpty()) {
            // no editor found for this file. likely an out-of-sequence issue
            // where Intellij is opening a project and doesn't fire events
            // in order we'd expect.
            return null
        }
        return editors[0] // hope just one
    }


    private fun isGrammarFile(project: Project, virtualFile: VirtualFile): Boolean {
        val previewState = AntlrService.getInstance(project).previewState(virtualFile)
        val g = previewState.g ?: return false
        val language = g.getOptionString(AntlrGrammarProperties.PROP_LANGUAGE)
        val generator = CodeGenerator.create(null, g, language)
        val recognizerFileName = generator.recognizerFileName
        val contentRoot = getContentRoot(project, virtualFile) ?: return false
        val antlrGrammarProperties = AntlrToolGrammarPropertiesStore.getGrammarProperties(project, virtualFile)
            ?: return false
        val pkg = antlrGrammarProperties.pkg
        val outputDirName = antlrGrammarProperties.resolveOutputDirName(project, contentRoot, pkg)
        val fullOutputFileName = outputDirName + File.separator + recognizerFileName
        val fullInputFileName =
            antlrGrammarProperties.resolveLibDir(project, getParentDir(virtualFile)) + File.separator + virtualFile.name

        val inFile = File(fullInputFileName)
        val outFile = File(fullOutputFileName)
        val stale = inFile.lastModified() > outFile.lastModified()
        return stale
    }


    fun antlr(project: Project, virtualFile: VirtualFile) {
        val args = getArgsAsList(project, virtualFile)
        val sourcePath = getParentDir(virtualFile)
        val fullyQualifiedInputFileName = sourcePath + File.separator + virtualFile.name
        args.add(fullyQualifiedInputFileName)
        val lexerGrammarFileName = ParsingUtils.getLexerNameFromParserFileName(fullyQualifiedInputFileName)
        if (File(lexerGrammarFileName).exists()) {
            args.add(lexerGrammarFileName)
        }
        log.info("Grammar generated from $fullyQualifiedInputFileName  args: $args")
        val tool = Tool(args.toTypedArray())
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)
        if (!project.isDisposed) {
            project.messageBus.syncPublisher(AntlrListener.TOPIC).print(
                timeStamp + ": antlr4 " + Misc.join(args.iterator(), " ") + "\n",
                ConsoleViewContentType.SYSTEM_OUTPUT
            )
        }
        tool.removeListeners()
        val listener = RunAntlrListener(tool, project)
        tool.addListener(listener)
        try {
            tool.processGrammarsOnCommandLine()
        } catch (e: Exception) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            val msg = sw.toString()
            val notification = Notification(
                GROUP_DISPLAY_ID,
                "can't generate parser for " + virtualFile.name,
                e.toString(),
                NotificationType.INFORMATION
            )
            Notifications.Bus.notify(notification, project)
            if (!project.isDisposed) {
                project.messageBus.syncPublisher(AntlrListener.TOPIC)
                    .print("$timeStamp: antlr4 $msg\n", ConsoleViewContentType.SYSTEM_OUTPUT)
            }
            listener.hasOutput = true
        }
        if (listener.hasOutput) {
            if (!project.isDisposed) {
                project.messageBus.syncPublisher(AntlrListener.TOPIC).activeConsoleView()
            }
        }
    }

    fun getArgsAsList(project: Project, file: VirtualFile): MutableList<String> {
        val argMap = getArgs(project, file)
        val args = mutableListOf<String>()
        for (arg in argMap) {
            args.add(arg.key)
            val value = arg.value
            if (value.isNotEmpty()) {
                args.add(value)
            }
        }
        return args
    }

    fun getArgs(project: Project, file: VirtualFile): Map<String, String> {
        val args = mutableMapOf<String, String>()
        val grammarProperties = AntlrToolGrammarPropertiesStore.getGrammarProperties(project, file)
        val sourcePath = getParentDir(virtualFile = file)
        var pkg: String? = null
        if (grammarProperties != null) {
            pkg = grammarProperties.getPkg()
        }
        if (pkg.isNullOrBlank() && !hasPackageDeclarationInHeader(project, file) && file.parent != null) {
            pkg = ProjectRootManager.getInstance(project).fileIndex.getPackageNameByDirectory(file.parent)
        }
        if (!pkg.isNullOrBlank()) {
            args["-package"] = pkg
        }
        val language = grammarProperties?.language
        if (language != null) {
            args["-Dlanguage=$language"] = ""
        }
        val contentRoot = getContentRoot(project, file)
        if (contentRoot != null) {
            val outputDirName = grammarProperties?.resolveOutputDirName(project, contentRoot, pkg)
            args["-o"] = if (outputDirName.isNullOrEmpty()) "" else outputDirName
        }
        var libDir = grammarProperties?.resolveLibDir(project, sourcePath)
        if (libDir != null) {
            val f = File(libDir)
            if (!f.isAbsolute && contentRoot != null) {
                libDir = contentRoot.path + File.separator + libDir
            }
            args["-lib"] = libDir
        }
        val encoding = grammarProperties?.encoding
        if (encoding != null) {
            args["-encoding"] = encoding
        }
        if (grammarProperties != null && grammarProperties.shouldGenerateParseTreeVisitor()) {
            args["-listener"] = ""
        } else {
            args["-no-listener"] = ""
        }
        if (grammarProperties != null && grammarProperties.shouldGenerateParseTreeVisitor()) {
            args["-visitor"] = ""
        } else {
            args["-no-visitor"] = ""
        }
        return args
    }

    fun getContentRoot(project: Project, file: VirtualFile): VirtualFile? {
        val virtualFileAtomicReference = AtomicReference<VirtualFile?>(null)
        val countDownLatch = CountDownLatch(1)
        ApplicationManager.getApplication().runReadAction {
            try {
                val root = ProjectRootManager.getInstance(project).fileIndex.getContentRootForFile(file)
                if (root != null) {
                    virtualFileAtomicReference.set(root)
                } else {
                    virtualFileAtomicReference.set(file.parent)
                }
            } finally {
                countDownLatch.countDown()
            }
        }
        try {
            countDownLatch.await(5L, TimeUnit.MINUTES)
        } catch (e: InterruptedException) {
            return null
        }
        return virtualFileAtomicReference.get()
    }

    private fun hasPackageDeclarationInHeader(project: Project, file: VirtualFile): Boolean {
        return ApplicationManager.getApplication().runReadAction(object : Computable<Boolean> {
            override fun compute(): Boolean {
                val psiFile = PsiManager.getInstance(project).findFile(file)
                val grammarSpecNode = PsiTreeUtil.getChildOfType(psiFile, GrammarSpecNode::class.java)
                if (grammarSpecNode != null) {
                    val prequelElementType =
                        AntlrTokenTypes.getRuleElementType(ANTLRv4Parser.RULE_prequelConstruct) ?: return false
                    for (prequelConstruct in findChildrenOfType(
                        grammarSpecNode,
                        prequelElementType
                    )) {
                        val anAction = PsiTreeUtil.getChildOfType(psiFile, AtAction::class.java)
                        if (anAction != null && anAction.idText == "header") {
                            return PACKAGE_DEFINITION_REGEX.matcher(anAction.actionBlockText).find()
                        }
                    }
                }
                return false
            }

        })
    }

    fun getParentDir(virtualFile: VirtualFile?): String? {
        if (virtualFile == null || virtualFile.parent == null) return null
        return virtualFile.parent.path
    }

    fun findChildrenOfType(psiElement: PsiElement, type: IElementType): Iterable<PsiElement> {
        return findChildrenOfType(psiElement, TokenSet.create(type))
    }

    private fun findChildrenOfType(psiElement: PsiElement, types: TokenSet): Iterable<PsiElement> {
        val psiElements = PsiTreeUtil.collectElements(psiElement, object : PsiElementFilter {
            override fun isAccepted(p0: PsiElement): Boolean {
                val node = p0.node ?: return false
                return types.contains(node.elementType)
            }
        })
        return psiElements.stream().toList()
    }

    fun currentEditorFileChangedEvent(project: Project?,oldFile:VirtualFile?,newFile:VirtualFile?,modified :Boolean){
        if(newFile == null) return
        if(newFile.extension.isNullOrBlank()) return
        if(!newFile.extension.equals("g4")){
            return
        }
        if(project==null){
            return
        }
        if(oldFile!=null && oldFile.extension?.equals("g4") == true && modified){
            AntlrService.getInstance(project).updateGrammar(oldFile,true)
        }
        val previewState = AntlrService.getInstance(project).previewState(newFile)
        if(previewState.g==null || previewState.lg==null){
            AntlrService.getInstance(project).updateGrammar(newFile,false)
        }
        if(!project.isDisposed){
            project.messageBus.syncPublisher(AntlrListener.TOPIC).grammarChange(newFile)
        }
    }
    fun startRuleNameEvent(project: Project,virtualFile: VirtualFile,ruleName:String){
        val previewState = AntlrService.getInstance(project).previewState(virtualFile)
        previewState.startRuleName=ruleName
        if(!project.isDisposed){
            project.messageBus.syncPublisher(AntlrListener.TOPIC).startRuleName(virtualFile,ruleName)
            project.messageBus.syncPublisher(AntlrListener.TOPIC).updateParseTreeState(virtualFile)
        }
    }
}
