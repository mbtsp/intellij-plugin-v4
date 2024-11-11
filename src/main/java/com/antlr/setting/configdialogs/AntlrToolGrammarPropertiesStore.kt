package com.antlr.setting.configdialogs

import com.antlr.language.psrsing.CaseChangingStrategy
import com.antlr.setting.configdialogs.AntlrToolGrammarPropertiesComponent.Companion.getInstance
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileTypes.WildcardFileNameMatcher
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.xmlb.annotations.Property

class AntlrToolGrammarPropertiesStore {
    @Property
    private val perGrammarGenerationSettings: MutableList<AntlrGrammarProperties> = ArrayList()

    fun add(properties: AntlrGrammarProperties?) {
        perGrammarGenerationSettings.add(properties!!)
    }

    fun getGrammarProperties(grammarFile: String): AntlrGrammarProperties {
        val grammarSettings = findSettingsForFile(grammarFile)

        if (grammarSettings == null) {
            val projectSettings = findSettingsForFile("*") ?: return DEFAULT_GRAMMAR_PROPERTIES

            return projectSettings
        }

        return grammarSettings
    }

    private fun getOrCreateGrammarProperties(grammarFile: String): AntlrGrammarProperties {
        val properties = getGrammarProperties(grammarFile)

        if (properties.fileName == grammarFile) {
            return properties
        }

        val newProperties = AntlrGrammarProperties(properties)
        newProperties.fileName = grammarFile

        add(newProperties)

        return newProperties
    }

    private fun findSettingsForFile(fileName: String): AntlrGrammarProperties? {
        for (settings in perGrammarGenerationSettings) {
            if (settings.fileName == fileName) {
                return settings
            }
        }

        for (settings in perGrammarGenerationSettings) {
            if (matchesWildcardPattern(fileName, settings)) {
                return settings
            }
        }

        return null
    }

    private fun matchesWildcardPattern(fileName: String, settings: AntlrGrammarProperties): Boolean {
        try {
            val wildcardFileNameMatcher = WildcardFileNameMatcher(settings.fileName!!)
            if (wildcardFileNameMatcher.acceptsCharSequence(fileName)) {
                return true
            }
        } catch (e: Exception) {
            logger.warn("Unable to check if wildcard matches file name: $fileName", e)
        }
        return false
    }

    companion object {
        private val logger = Logger.getInstance(AntlrToolGrammarPropertiesStore::class.java)

        val DEFAULT_GRAMMAR_PROPERTIES: AntlrGrammarProperties = initDefaultGrammarProperties()

        @JvmStatic
        fun getGrammarProperties(project: Project, grammarFile: VirtualFile): AntlrGrammarProperties? {
            return getGrammarProperties(project, grammarFile.path)
        }

        /**
         * Defaults to settings defined in the project if they exist, or to empty settings.
         */
        @JvmStatic
        fun getGrammarProperties(project: Project, grammarFile: String): AntlrGrammarProperties? {
            val antlrToolGrammarPropertiesComponent = getInstance(project) ?: return null
            val store = antlrToolGrammarPropertiesComponent.state
            return store.getGrammarProperties(grammarFile)
        }

        /**
         * Get the properties for this grammar, or create a new properties object derived from the project settings if
         * they exist, or from the default empty settings otherwise.
         */
        fun getOrCreateGrammarProperties(project: Project, grammarFile: String): AntlrGrammarProperties? {
            val antlrToolGrammarPropertiesComponent = getInstance(project)?:return null
            val store = antlrToolGrammarPropertiesComponent.state
            return store.getOrCreateGrammarProperties(grammarFile)
        }

        private fun initDefaultGrammarProperties(): AntlrGrammarProperties {
            val defaultSettings = AntlrGrammarProperties()

            defaultSettings.fileName = "**"
            defaultSettings.autoGen = true
            defaultSettings.outputDir = ""
            defaultSettings.libDir = ""
            defaultSettings.encoding = ""
            defaultSettings.pkg = ""
            defaultSettings.language = ""
            defaultSettings.generateListener = true
            defaultSettings.generateVisitor = true
            defaultSettings.caseChangingStrategy = CaseChangingStrategy.LEAVE_AS_IS

            return defaultSettings
        }
    }
}
