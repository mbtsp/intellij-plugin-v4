package com.antlr.setting.configdialogs

import com.antlr.language.psrsing.CaseChangingStrategy
import com.antlr.language.psrsing.RunAntlrOnGrammarFile
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.annotations.Property
import org.apache.commons.lang3.StringUtils
import java.io.File

class AntlrGrammarProperties : Cloneable {
    @JvmField
    @Property
    var fileName: String? = null

    @JvmField
    @Property
    var autoGen: Boolean = false

    @JvmField
    @Property
    var outputDir: String? = null

    @JvmField
    @Property
    var libDir: String? = null

    @JvmField
    @Property
    var encoding: String? = null

    @Property
    @JvmField
    var pkg: String? = null

    @JvmField
    @Property
    var language: String? = null

    @JvmField
    @Property
    var generateListener: Boolean = true

    @JvmField
    @Property
    var generateVisitor: Boolean = false

    @JvmField
    @Property
    @OptionTag(converter = CaseChangingStrategyConverter::class)
    var caseChangingStrategy: CaseChangingStrategy? = CaseChangingStrategy.LEAVE_AS_IS

    constructor()

    constructor(source: AntlrGrammarProperties) {
        this.fileName = source.fileName
        this.autoGen = source.autoGen
        this.outputDir = source.outputDir
        this.libDir = source.libDir
        this.encoding = source.encoding
        this.pkg = source.pkg
        this.language = source.language
        this.generateListener = source.generateListener
        this.generateVisitor = source.generateVisitor
        this.caseChangingStrategy = source.caseChangingStrategy
    }

    fun shouldAutoGenerateParser(): Boolean {
        return autoGen
    }

    fun getOutputDir(): String? {
        return outputDir
    }

    fun getLibDir(): String? {
        return libDir
    }

    fun shouldGenerateParseTreeListener(): Boolean {
        return generateListener
    }

    fun shouldGenerateParseTreeVisitor(): Boolean {
        return generateVisitor
    }

    fun resolveOutputDirName(project: Project, contentRoot: VirtualFile, pkg: String?): String {
        var outputDirName = (if (outputDir.isNullOrEmpty()) RunAntlrOnGrammarFile.OUTPUT_DIR_NAME else outputDir)!!

        outputDirName = PathMacroManager.getInstance(project).expandPath(outputDirName)

        val f = File(outputDirName)
        if (!f.isAbsolute) { // if not absolute file spec, it's relative to project root
            outputDirName = contentRoot.path + File.separator + outputDirName
        }
        // add package if any
        if (StringUtils.isNotBlank(pkg)) {
            outputDirName += File.separator + pkg!!.replace('.', File.separatorChar)
        }
        return outputDirName
    }

    fun resolveLibDir(project: Project, defaultValue: String?): String? {
        var libDir = this.libDir

        if (libDir == null || libDir.isEmpty()) {
            libDir = defaultValue
        }

        return PathMacroManager.getInstance(project).expandPath(libDir)
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): AntlrGrammarProperties {
        return super.clone() as AntlrGrammarProperties
    }

    fun getEncoding(): String? {
        return encoding
    }

    fun getLanguage(): String? {
        return language
    }

    fun getPkg(): String? {
        return pkg
    }

    fun getCaseChangingStrategy(): CaseChangingStrategy? {
        return caseChangingStrategy
    }

//    companion object {
//        const val PROP_LANGUAGE: String = "language"
//        const val PROJECT_SETTINGS_PREFIX: String = "*"
//    }
}
