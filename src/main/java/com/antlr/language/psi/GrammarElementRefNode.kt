package com.antlr.language.psi

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType

/**
 * Refs to tokens, rules
 */
abstract class GrammarElementRefNode(type: IElementType, text: CharSequence) : LeafPsiElement(type, text) {
    override fun getName(): String? {
        return text
    }

    override fun toString(): String {
        return javaClass.getSimpleName() + "(" + elementType + ")"
    }
}
