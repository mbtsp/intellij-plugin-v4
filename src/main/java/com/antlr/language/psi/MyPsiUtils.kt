package com.antlr.language.psi

import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrFileRoot
import com.antlr.language.AntlrLanguage
import com.antlr.language.AntlrTokenTypes
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil

object MyPsiUtils {
    fun findFirstChildOfType(parent: PsiElement?, type: IElementType): PsiElement? {
        return findFirstChildOfType(parent, TokenSet.create(type))
    }

    /**
     * traverses the psi tree depth-first, returning the first it finds with the given types
     * @param parent the element whose children will be searched
     * @param types the types to search for
     * @return the first child, or null;
     */
    fun findFirstChildOfType(parent: PsiElement?, types: TokenSet): PsiElement? {
        val iterator = findChildrenOfType(parent, types).iterator()
        if (iterator.hasNext()) return iterator.next()
        return null
    }

    fun findChildrenOfType(parent: PsiElement?, type: IElementType): Iterable<PsiElement?> {
        return findChildrenOfType(parent, TokenSet.create(type))
    }

    /**
     * Like PsiTreeUtil.findChildrenOfType, except no collection is created and it doesnt use recursion.
     * @param parent the element whose children will be searched
     * @param types the types to search for
     * @return an iterable that will traverse the psi tree depth-first, including only the elements
     * whose type is contained in the provided tokenset.
     */
    fun findChildrenOfType(parent: PsiElement?, types: TokenSet): Iterable<PsiElement?> {
        val psiElements = PsiTreeUtil.collectElements(parent, PsiElementFilter { input: PsiElement? ->
            val node = input!!.node
            if (node == null) return@PsiElementFilter false
            types.contains(node.elementType)
        })
        return listOf<PsiElement?>(*psiElements)
    }

    /**
     * Finds the first [RuleSpecNode] or [ModeSpecNode] matching the `ruleName` defined in
     * the given `grammar`.
     *
     * Rule specs can be either children of the [RulesNode], or under one of the `mode`s defined in
     * the grammar. This means we have to walk the whole grammar to find matching candidates.
     */
    @JvmStatic
    fun findSpecNode(grammar: GrammarSpecNode?, ruleName: String?): PsiElement? {
        val definitionFilter = PsiElementFilter { element1: PsiElement? ->
            if (element1 !is RuleSpecNode) {
                return@PsiElementFilter false
            }
            val id = element1.getNameIdentifier()
            id != null && id.text == ruleName
        }

        val ruleSpec = PsiTreeUtil.collectElements(grammar, definitionFilter)
        if (ruleSpec.size > 0) {
            return ruleSpec[0]
        }
        return null
    }

    @JvmStatic
    fun createLeafFromText(
        project: Project, context: PsiElement?,
        text: String?, type: IElementType
    ): PsiElement? {
        val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl
        val el = factory.createElementFromText(
            text,
            AntlrLanguage.INSTANCE,
            type,
            context
        )
        if (el == null) {
            return null
        }
        return PsiTreeUtil.getDeepestFirst(el) // forces parsing of file!!
        // start rule depends on root passed in
    }

//    fun replacePsiFileFromText(project: Project, psiFile: PsiFile, text: String) {
//        val newPsiFile = createFile(project, text)
//        val setTextAction: WriteCommandAction<*> = object : WriteCommandAction<Any?>(project) {
//            override fun run(result: Result<*>?) {
//                psiFile.deleteChildRange(psiFile.firstChild, psiFile.lastChild)
//                psiFile.addRange(newPsiFile.firstChild, newPsiFile.lastChild)
//            }
//        }
//        setTextAction.execute()
//    }

    fun createFile(project: Project, text: String): PsiFile {
        val fileName = "a.g4" // random name but must be .g4
        val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl
        return factory.createFileFromText(
            fileName, AntlrLanguage.INSTANCE,
            text, false, false
        )
    }

    fun collectAtActions(root: PsiElement?, tokenText: String?): Array<PsiElement> {
        return PsiTreeUtil.collectElements(root, PsiElementFilter { element: PsiElement? ->
            var p = element!!.context
            if (p != null) p = p.context
            p is AtAction &&
                    element is ParserRuleRefNode &&
                    element.text == tokenText
        })
    }

    /** Search all internal and leaf nodes looking for token or internal node
     * with specific text.
     * This saves having to create lots of java classes just to identify psi nodes.
     */
    fun collectNodesWithName(root: PsiElement?, tokenText: String?): Array<PsiElement> {
        return PsiTreeUtil.collectElements(root, PsiElementFilter { element: PsiElement? ->
            val tokenTypeName = element!!.node.elementType.toString()
            tokenTypeName == tokenText
        })
    }

    fun collectNodesWithText(root: PsiElement?, text: String?): Array<PsiElement> {
        return PsiTreeUtil.collectElements(
            root,
            PsiElementFilter { element: PsiElement? -> element!!.text == text })
    }

    fun collectChildrenOfType(root: PsiElement, tokenType: IElementType?): Array<PsiElement?> {
        val elems: MutableList<PsiElement?> = ArrayList<PsiElement?>()
        for (child in root.children) {
            if (child.node.elementType === tokenType) {
                elems.add(child)
            }
        }
        return elems.toTypedArray<PsiElement?>()
    }

    fun findChildOfType(root: PsiElement, tokenType: IElementType?): PsiElement? {
        for (child in root.children) {
            if (child.node.elementType === tokenType) {
                return child
            }
        }
        return null
    }

    fun collectChildrenWithText(root: PsiElement, text: String?): Array<PsiElement?> {
        val elems: MutableList<PsiElement?> = ArrayList<PsiElement?>()
        for (child in root.children) {
            if (child.text == text) {
                elems.add(child)
            }
        }
        return elems.toTypedArray<PsiElement?>()
    }

    // Look for stuff like: options { tokenVocab=ANTLRv4Lexer; superClass=Foo; }
    fun findTokenVocabIfAny(file: AntlrFileRoot?): String? {
        var vocabName: String? = null
        val options = collectNodesWithName(file, "option")
        for (o in options) {
            val tokenVocab = collectChildrenWithText(o, "tokenVocab")
            if (tokenVocab.isNotEmpty()) {
                val optionNode = tokenVocab[0]!!.parent // tokenVocab[0] is id node
                val ids = collectChildrenOfType(
                    optionNode,
                    AntlrTokenTypes.RULE_ELEMENT_TYPES[ANTLRv4Parser.RULE_optionValue]
                )
                vocabName = ids[0]!!.text
            }
        }
        return vocabName
    }
}
