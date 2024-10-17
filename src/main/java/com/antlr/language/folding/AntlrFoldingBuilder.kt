package com.antlr.language.folding

import com.antlr.language.ANTLRv4Lexer
import com.antlr.language.ANTLRv4Parser
import com.antlr.language.AntlrFileRoot
import com.antlr.language.AntlrTokenTypes
import com.antlr.language.AntlrTokenTypes.getRuleElementType
import com.antlr.language.AntlrTokenTypes.getTokenElementType
import com.antlr.language.psi.AtAction
import com.antlr.language.psi.GrammarElementRefNode
import com.antlr.language.psi.ModeSpecNode
import com.antlr.language.psi.MyPsiUtils.findChildrenOfType
import com.antlr.language.psi.MyPsiUtils.findFirstChildOfType
import com.antlr.language.psi.RuleSpecNode
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.UnfairTextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.SourceTreeToPsiMap
import com.intellij.psi.impl.source.tree.TreeUtil
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil

class AntlrFoldingBuilder : CustomFoldingBuilder() {
    companion object {
        private val DOC_COMMENT_TOKEN = getTokenElementType(ANTLRv4Lexer.DOC_COMMENT)
        private val BLOCK_COMMENT_TOKEN = getTokenElementType(ANTLRv4Lexer.BLOCK_COMMENT)
        private val LINE_COMMENT_TOKEN = getTokenElementType(ANTLRv4Lexer.LINE_COMMENT)

        private val OPTIONSSPEC = getRuleElementType(ANTLRv4Parser.RULE_optionsSpec)
        private val OPTIONS = getTokenElementType(ANTLRv4Lexer.OPTIONS)

        private val TOKENSSPEC = getRuleElementType(ANTLRv4Parser.RULE_tokensSpec)
        private val TOKENS = getTokenElementType(ANTLRv4Lexer.TOKENS)

        private val RBRACE = getTokenElementType(ANTLRv4Lexer.RBRACE)
        private val SEMICOLON = getTokenElementType(ANTLRv4Lexer.SEMI)

        private val RULE_BLOCKS = TokenSet.create(
            getRuleElementType(ANTLRv4Parser.RULE_lexerBlock),
            getRuleElementType(ANTLRv4Parser.RULE_ruleBlock)
        )


        private fun addTokensFoldingDescriptor(descriptors: MutableList<FoldingDescriptor?>, root: PsiElement?) {
            val tokensSpec: PsiElement? = findFirstChildOfType(root, TOKENSSPEC)
            if (tokensSpec != null) {
                val tokens = tokensSpec.firstChild
                if (tokens.node.elementType === TOKENS) {
                    val rbrace = tokensSpec.lastChild
                    if (rbrace.node.elementType === RBRACE) {
                        descriptors.add(
                            FoldingDescriptor(
                                tokensSpec,
                                TextRange(tokens.textRange.endOffset, rbrace.textRange.endOffset)
                            )
                        )
                    }
                }
            }
        }

        private fun addOptionsFoldingDescriptor(descriptors: MutableList<FoldingDescriptor?>, root: PsiElement?) {
            val optionsSpec: PsiElement? = findFirstChildOfType(root, OPTIONSSPEC)
            if (optionsSpec != null) {
                val options = optionsSpec.firstChild
                if (options.node.elementType === OPTIONS) {
                    val rbrace = optionsSpec.lastChild
                    if (rbrace.node.elementType === RBRACE) {
                        descriptors.add(
                            FoldingDescriptor(
                                optionsSpec,
                                TextRange(options.textRange.endOffset, rbrace.textRange.endOffset)
                            )
                        )
                    }
                }
            }
        }

        private fun addHeaderFoldingDescriptor(
            descriptors: MutableList<FoldingDescriptor?>,
            root: PsiElement,
            document: Document
        ) {
            val range: TextRange? = getFileHeader(root)
            if (range != null && range.length > 1 && document.getLineNumber(range.endOffset) > document.getLineNumber(
                    range.startOffset
                )
            ) {
                descriptors.add(FoldingDescriptor(root, range))
            }
        }

        private fun addCommentDescriptors(descriptors: MutableList<FoldingDescriptor?>, root: PsiElement?) {
            val processedComments: MutableSet<PsiElement?> = HashSet<PsiElement?>()
            for (comment in findChildrenOfType(root, AntlrTokenTypes.COMMENTS)) {
                val type = comment!!.node.elementType
                if (processedComments.contains(comment)) continue
                if (type === DOC_COMMENT_TOKEN || type === BLOCK_COMMENT_TOKEN) {
                    descriptors.add(FoldingDescriptor(comment, comment.textRange))
                }
                //addCommentFolds(comment, processedComments, descriptors);
            }
        }

        private fun addActionFoldingDescriptors(descriptors: MutableList<FoldingDescriptor?>, root: PsiElement?) {
            for (atAction in PsiTreeUtil.findChildrenOfType<AtAction>(root, AtAction::class.java)) {
                val action = atAction.lastChild
                val actionText = action.text
                if (actionText != null && actionText.contains("\n")) {
                    descriptors.add(FoldingDescriptor(atAction, action.textRange))
                }
            }
        }

        private fun addRuleRefFoldingDescriptors(descriptors: MutableList<FoldingDescriptor?>, root: PsiElement?) {
            for (specNode in PsiTreeUtil.findChildrenOfType<RuleSpecNode?>(root, RuleSpecNode::class.java)) {
                val refNode =
                    PsiTreeUtil.findChildOfAnyType<GrammarElementRefNode?>(specNode, GrammarElementRefNode::class.java)
                if (refNode == null) continue
                val nextSibling = refNode.nextSibling
                if (nextSibling == null) continue
                val startOffset = nextSibling.textOffset

                val backward = TreeUtil.findChildBackward(specNode.node, SEMICOLON)
                if (backward == null) continue
                val endOffset = backward.textRange.endOffset
                if (startOffset >= endOffset) continue

                descriptors.add(FoldingDescriptor(specNode, TextRange(startOffset, endOffset)))
            }
        }

        private fun addModeFoldingDescriptors(descriptors: MutableList<FoldingDescriptor?>, root: PsiElement?) {
            for (specNode in PsiTreeUtil.findChildrenOfType<ModeSpecNode>(root, ModeSpecNode::class.java)) {
                val semi = findFirstChildOfType(specNode, getTokenElementType(ANTLRv4Lexer.SEMI))

                if (semi != null) {
                    val foldingRange = TextRange.create(
                        semi.textOffset,
                        specNode.node.startOffset + specNode.textLength
                    )
                    descriptors.add(FoldingDescriptor(specNode, foldingRange))
                }
            }
        }

        private fun isComment(element: PsiElement): Boolean {
            val type = element.node.elementType
            return AntlrTokenTypes.COMMENTS.contains(type)
        }

        private fun getFileHeader(file: PsiElement): TextRange? {
            var first = file.firstChild
            if (first is PsiWhiteSpace) first = first.nextSibling
            var element = first
            while (isComment(element!!)) {
                element = element.nextSibling
                if (element is PsiWhiteSpace) {
                    element = element.nextSibling
                } else {
                    break
                }
            }
            if (element == null) return null
            if (element.prevSibling is PsiWhiteSpace) element = element.prevSibling
            if (element == null || element == first) return null
            return UnfairTextRange(first!!.textOffset, element.textOffset)
        }

        private fun getPlaceholderText(element: PsiElement?): String {
            if (element == null) {
                return "..."
            }
            if (element.node.elementType === LINE_COMMENT_TOKEN) {
                return "//..."
            } else if (element is ModeSpecNode) {
                return ";..."
            } else if (element is RuleSpecNode) {
                return ":...;"
            } else if (element is AtAction) {
                return "{...}"
            }
            return "..."
        }
    }

    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor?>,
        root: PsiElement,
        document: Document,
        quick: Boolean
    ) {
        if (root !is AntlrFileRoot) return

        addRuleRefFoldingDescriptors(descriptors, root)

        addActionFoldingDescriptors(descriptors, root)

        addHeaderFoldingDescriptor(descriptors, root, document)

        addCommentDescriptors(descriptors, root)

        addOptionsFoldingDescriptor(descriptors, root)

        addTokensFoldingDescriptor(descriptors, root)

        addModeFoldingDescriptors(descriptors, root)
    }

    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        return getPlaceholderText(SourceTreeToPsiMap.treeElementToPsi(node))
    }

    override fun isRegionCollapsedByDefault(node: ASTNode): Boolean {
        val element = SourceTreeToPsiMap.treeElementToPsi(node)
        if (element == null) return false

        val settings: AntlrFoldingSettings = AntlrFoldingSettings.instance

        if (RULE_BLOCKS.contains(node.elementType)) return settings.isCollapseRuleBlocks
        if (node.elementType === TOKENSSPEC) return settings.isCollapseTokens

        if (element is AtAction) return settings.isCollapseActions

        if (element is AntlrFileRoot) {
            return settings.isCollapseFileHeader
        }
        if (node.elementType === DOC_COMMENT_TOKEN) {
            val parent = element.parent

            if (parent is AntlrFileRoot) {
                var firstChild = parent.firstChild
                if (firstChild is PsiWhiteSpace) {
                    firstChild = firstChild.nextSibling
                }
                if (element == firstChild) {
                    return settings.isCollapseFileHeader
                }
            }
            return settings.isCollapseDocComments
        }
        if (isComment(element)) {
            return settings.isCollapseComments
        }
        return false
    }


}
