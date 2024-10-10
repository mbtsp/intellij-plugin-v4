package com.antlr.language

import com.intellij.lang.CodeDocumentationAwareCommenter
import com.intellij.psi.PsiComment
import com.intellij.psi.tree.IElementType

class AntlrCommenter : CodeDocumentationAwareCommenter {
    override fun getLineCommentPrefix(): String? {
        return "//"
    }

    override fun getBlockCommentPrefix(): String? {
        return "/*"
    }

    override fun getBlockCommentSuffix(): String? {
        return "*/"
    }

    override fun getCommentedBlockCommentPrefix(): String? {
        return null
    }

    override fun getCommentedBlockCommentSuffix(): String? {
        return null
    }

    override fun getLineCommentTokenType(): IElementType? {
        return ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.LINE_COMMENT]
    }

    override fun getBlockCommentTokenType(): IElementType? {
        return ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.BLOCK_COMMENT]
    }

    override fun getDocumentationCommentTokenType(): IElementType? {
        return ANTLRv4TokenTypes.TOKEN_ELEMENT_TYPES[ANTLRv4Lexer.DOC_COMMENT]
    }

    override fun getDocumentationCommentPrefix(): String? {
        return "/**"
    }

    override fun getDocumentationCommentLinePrefix(): String? {
        //TODO: this isnt specified in the grammar. remove?
        return "*"
    }

    override fun getDocumentationCommentSuffix(): String? {
        return "*/"
    }

    override fun isDocumentationComment(element: PsiComment?): Boolean {
        return element != null && element.tokenType === documentationCommentTokenType
    }
}
