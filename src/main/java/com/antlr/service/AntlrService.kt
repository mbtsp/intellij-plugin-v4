package com.antlr.service

import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrTokenTypes
import com.antlr.language.psi.AtAction
import com.antlr.language.psi.GrammarSpecNode
import com.antlr.language.psrsing.ParsingUtils
import com.antlr.language.psrsing.RunAntlrListener
import com.antlr.listener.AntlrListener
import com.antlr.preview.PreviewState
import com.antlr.setting.configdialogs.AntlrGrammarProperties
import com.antlr.setting.configdialogs.AntlrToolGrammarPropertiesStore
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ThrowableRunnable
import com.intellij.util.containers.stream
import org.antlr.v4.Tool
import org.antlr.v4.codegen.CodeGenerator
import org.stringtemplate.v4.misc.Misc
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@Service(Service.Level.PROJECT)
class AntlrService(private val project: Project) {
    val grammarToPreviewState = mutableMapOf<String, PreviewState>()
    val groupDisplayId = "ANTLR Tool Parser Generation"

    val logger = Logger.getInstance(AntlrService::class.java)
    val PACKAGE_DEFINITION_REGEX = Pattern.compile("package\\s+[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_];");
    fun generateCode(virtualFile: VirtualFile, forceGenerateCode: Boolean): String {
        val antlrGrammarProperties = AntlrToolGrammarPropertiesStore.getGrammarProperties(project, virtualFile)
        var autoGen = false
        if (antlrGrammarProperties != null) {
            autoGen = antlrGrammarProperties.autoGen
        }
        if (forceGenerateCode || autoGen && isGrammarFile(virtualFile)) {
            ReadAction.run(object :ThrowableRunnable(){

            })

        }

    }

    fun antlr(virtualFile: VirtualFile) {
        val args = getAntlrArgsList(virtualFile)
        val sourcePath = getParentDir(virtualFile)
        val fullyQualifiedInputFileName = sourcePath + File.separator + virtualFile.name
        args.add(fullyQualifiedInputFileName)
        val lexerGrammarFileName = ParsingUtils.getLexerNameFromParserFileName(fullyQualifiedInputFileName)
        if (File(lexerGrammarFileName).exists()) {
            args.add(lexerGrammarFileName)
        }
        logger.info("Grammar generated from $fullyQualifiedInputFileName  args: $args")
        val tool = Tool(args.toTypedArray())
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())
        if (!project.isDisposed) {
            project.messageBus.syncPublisher<AntlrListener>(AntlrListener.TOPIC).print(
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
                groupDisplayId,
                "can't generate parser for " + virtualFile.name,
                e.toString(),
                NotificationType.INFORMATION
            )
            Notifications.Bus.notify(notification, project)
            if (!project.isDisposed) {
                project.messageBus.syncPublisher<AntlrListener>(AntlrListener.TOPIC)
                    .print("$timeStamp: antlr4 $msg\n", ConsoleViewContentType.SYSTEM_OUTPUT)
            }
            listener.hasOutput = true
        }
        if (listener.hasOutput) {
            if (!project.isDisposed) {
                project.messageBus.syncPublisher<AntlrListener>(AntlrListener.TOPIC).activeConsoleView()
            }
        }
    }

    fun getAntlrArgsList(virtualFile: VirtualFile): MutableList<String> {
        val argMap = getAntlrArgs(virtualFile)
        val args = mutableListOf<String>()
        for ((key, value) in argMap) {
            args.add(key)
            if (value.isNotBlank()) {
                args.add(value)
            }
        }
        return args
    }

    fun getAntlrArgs(virtualFile: VirtualFile): Map<String, String> {
        val args = mutableMapOf<String, String>()
        val grammarProperties = AntlrToolGrammarPropertiesStore.getGrammarProperties(project, virtualFile)
        val sourcePath = getParentDir(virtualFile)
        var pkg: String? = null
        if (grammarProperties != null) {
            pkg = grammarProperties.getPkg()
        }
        if (pkg.isNullOrBlank() && !hasPackageDeclarationInHeader(project, virtualFile) && virtualFile.parent != null) {
            pkg = ProjectRootManager.getInstance(project).fileIndex.getPackageNameByDirectory(virtualFile.parent)
        }
        if (!pkg.isNullOrBlank()) {
            args.put("-package", pkg)
        }

        val language = grammarProperties?.getLanguage()
        if (!language.isNullOrBlank()) {
            args.put("-Dlanguage=$language", "")
        }

        // create gen dir at root of project by default, but add in package if any
        val contentRoot = getContentRoot(virtualFile)
        if (contentRoot != null) {
            val outputDirName = grammarProperties?.resolveOutputDirName(project, contentRoot, pkg)
            args.put("-o", outputDirName.toString())
        }

        var libDir = grammarProperties?.resolveLibDir(project, sourcePath)
        if (libDir != null) {
            val f = File(libDir)
            if (!f.isAbsolute && contentRoot != null) { // if not absolute file spec, it's relative to project root
                libDir = contentRoot.path + File.separator + libDir
            }
            args.put("-lib", libDir)
        }


        val encoding = grammarProperties?.getEncoding()
        if (!encoding.isNullOrBlank()) {
            args.put("-encoding", encoding)
        }

        if (grammarProperties != null && grammarProperties.shouldGenerateParseTreeListener()) {
            args.put("-listener", "")
        } else {
            args.put("-no-listener", "")
        }
        if (grammarProperties != null && grammarProperties.shouldGenerateParseTreeVisitor()) {
            args.put("-visitor", "")
        } else {
            args.put("-no-visitor", "")
        }
        return args
    }

    fun hasPackageDeclarationInHeader(project: Project, vfile: VirtualFile): Boolean {
        val file = PsiManager.getInstance(project).findFile(vfile)
        val grammarSpecNode =
            PsiTreeUtil.getChildOfType<GrammarSpecNode>(file, GrammarSpecNode::class.java) ?: return false
        val ruleIElementType = AntlrTokenTypes.getRuleElementType(ANTLRv4Parser.RULE_prequelConstruct)
        val psiElements = findChildrenOfType(grammarSpecNode, ruleIElementType)
        psiElements.forEach {
            val atAction = PsiTreeUtil.getChildOfType<AtAction>(ruleIElementType as PsiElement?, AtAction::class.java)
            if (atAction != null && atAction.idText == "header") {
                return PACKAGE_DEFINITION_REGEX.matcher(atAction.actionBlockText).find()
            }
        }
        return false

    }

    fun findChildrenOfType(psiElement: PsiElement, type: IElementType): Iterable<PsiElement> {
        return findChildrenOfType(psiElement, TokenSet.create(type))
    }

    fun findChildrenOfType(psiElement: PsiElement, types: TokenSet): Iterable<PsiElement> {
        val psiElements = PsiTreeUtil.collectElements(psiElement, object : PsiElementFilter {
            override fun isAccepted(p0: PsiElement): Boolean {
                val node = p0.node ?: return false
                return types.contains(node.elementType)
            }
        })
        return psiElements.stream().toList()
    }

    fun isGrammarFile(virtualFile: VirtualFile): Boolean {
        val previewState = previewState(virtualFile)
        val g = previewState.g
        if (g == null) {
            return false
        }
        val language = g.getOptionString(AntlrGrammarProperties.PROP_LANGUAGE)
        val generator = CodeGenerator.create(null, g, language)
        val recognizerFileName = generator.recognizerFileName
        val contentRoot = getContentRoot(virtualFile) ?: return false
        val antlrGrammarProperties = AntlrToolGrammarPropertiesStore.getGrammarProperties(project, virtualFile)
        if (antlrGrammarProperties == null) {
            return false
        }
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

    fun getParentDir(virtualFile: VirtualFile?): String? {
        if (virtualFile == null || virtualFile.parent == null) {
            return null
        }
        return virtualFile.parent.path
    }

    fun getContentRoot(virtualFile: VirtualFile): VirtualFile? {
        val file = ProjectRootManager.getInstance(project).fileIndex.getContentRootForFile(virtualFile)
        if (file != null) {
            return file
        }
        return virtualFile.parent
    }


    fun previewState(virtualFile: VirtualFile): PreviewState {
        if (!grammarToPreviewState.containsKey(virtualFile.path)) {
            val previewState = PreviewState(project, virtualFile)
            grammarToPreviewState.put(virtualFile.path, previewState)
            return previewState
        }
        return grammarToPreviewState[virtualFile.path]!!
    }
}
