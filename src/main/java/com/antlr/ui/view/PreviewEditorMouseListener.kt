package com.antlr.ui.view

import com.antlr.preview.PreviewState
import com.antlr.ui.InputPanel
import com.antlr.ui.ShowAmbigTreesDialog
import com.antlr.util.AntlrUtil.getMouseOffset
import com.antlr.util.getHighlighterWithDecisionEventType
import com.antlr.util.getRangeHighlightersAtOffset
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseEventArea
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.ui.popup.JBPopup
import org.antlr.v4.runtime.atn.AmbiguityInfo
import org.antlr.v4.runtime.atn.LookaheadEventInfo
import java.awt.event.MouseEvent

internal class PreviewEditorMouseListener(private val inputPanel: InputPanel) : EditorMouseListener,
    EditorMouseMotionListener {
    override fun mouseExited(e: EditorMouseEvent) {
        InputPanel.clearTokenInfoHighlighters(e.editor)
    }

    override fun mouseClicked(e: EditorMouseEvent) {
        val offset = getEditorCharOffsetAndRemoveTokenHighlighters(e)
        if (offset < 0) return

        val editor = e.editor
        if (inputPanel.previewState == null) {
            return
        }

        if (e.mouseEvent.button == MouseEvent.BUTTON3) { // right click
            rightClick(inputPanel.previewState, editor, offset)
            return
        }

        val mouseEvent = e.mouseEvent
        if (mouseEvent.isControlDown) {
            inputPanel.setCursorToGrammarElement(e.editor.project, inputPanel.previewState, offset)
            inputPanel.setCursorToHierarchyViewElement(offset)
        } else if (mouseEvent.isAltDown) {
            inputPanel.setCursorToGrammarRule(e.editor.project, inputPanel.previewState, offset)
        } else {
            inputPanel.setCursorToHierarchyViewElement(offset)
        }
        InputPanel.clearDecisionEventHighlighters(editor)
    }

    fun rightClick(previewState: PreviewState, editor: Editor, offset: Int) {
        if (previewState.parsingResult == null) return
        val highlightersAtOffset: List<RangeHighlighter> = editor.getRangeHighlightersAtOffset(offset)
        if (highlightersAtOffset.isEmpty()) {
            return
        }

        val ambigInfo =
            getHighlighterWithDecisionEventType(
                highlightersAtOffset,
                AmbiguityInfo::class.java
            )
        val lookaheadInfo =
            getHighlighterWithDecisionEventType(
                highlightersAtOffset,
                LookaheadEventInfo::class.java
            )
        if (ambigInfo != null) {
            val popup: JBPopup = ShowAmbigTreesDialog.createAmbigTreesPopup(previewState, ambigInfo as AmbiguityInfo)
            popup.showInBestPositionFor(editor)
        } else if (lookaheadInfo != null) {
            val popup: JBPopup = ShowAmbigTreesDialog.createLookaheadTreesPopup(previewState, lookaheadInfo as LookaheadEventInfo)
            popup.showInBestPositionFor(editor)
        }
    }

    override fun mouseMoved(e: EditorMouseEvent) {
        val offset = getEditorCharOffsetAndRemoveTokenHighlighters(e)
        if (offset < 0) return

        val editor = e.editor
        if (inputPanel.previewState == null) {
            return
        }

        val mouseEvent = e.mouseEvent
        InputPanel.clearTokenInfoHighlighters(e.editor)
        if (mouseEvent.isControlDown && inputPanel.previewState.parsingResult != null) {
            inputPanel.showTokenInfoUponCtrlKey(editor, inputPanel.previewState, offset)
        } else if (mouseEvent.isAltDown && inputPanel.previewState.parsingResult != null) {
            inputPanel.showParseRegion(editor, inputPanel.previewState, offset)
        } else { // just moving around, show any errors or hints
            InputPanel.showTooltips(editor, inputPanel.previewState, offset)
        }
    }

    private fun getEditorCharOffsetAndRemoveTokenHighlighters(e: EditorMouseEvent): Int {
        if (e.area != EditorMouseEventArea.EDITING_AREA) {
            return -1
        }

        val mouseEvent = e.mouseEvent
        val editor = e.editor
        val offset: Int = getMouseOffset(mouseEvent, editor)

        if (offset >= editor.document.textLength) {
            return -1
        }

        // Mouse has moved so make sure we don't show any token information tooltips
        InputPanel.clearTokenInfoHighlighters(e.editor)
        return offset
    }

    // ------------------------
    override fun mousePressed(e: EditorMouseEvent) {
    }

    override fun mouseReleased(e: EditorMouseEvent) {
    }

    override fun mouseEntered(e: EditorMouseEvent) {
    }

    override fun mouseDragged(e: EditorMouseEvent) {
    }
}
