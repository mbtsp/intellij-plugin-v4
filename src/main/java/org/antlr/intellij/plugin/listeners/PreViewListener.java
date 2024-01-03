package org.antlr.intellij.plugin.listeners;

import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.vfs.VirtualFile;
import org.antlr.intellij.plugin.preview.PreviewState;
import org.jetbrains.annotations.Nullable;

import java.util.EventListener;

public interface PreViewListener extends EventListener {
    void releaseEditor(PreviewState previewState);

    void setStartRuleName(VirtualFile grammarFile, String startRuleName);

    void updateParseTreeFromDoc(VirtualFile grammarFile);

    void grammarFileSaved(VirtualFile grammarFile);

    void grammarFileChanged(VirtualFile grammarFile);

    void mouseEnteredGrammarEditorEvent(VirtualFile file, EditorMouseEvent event);

    void closeGrammar(VirtualFile file);

    void setEnabled(boolean enabled);

    void toolWindowHide(@Nullable Runnable runnable);

    void onParsingCompleted(PreviewState previewState, long duration);

    void notifySlowParsing();

    void onParsingCancelled();

    void clearParseErrors();

    void startParsing();

    void autoRefreshPreview(VirtualFile virtualFile);

}
