package com.antlr.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.vfs.VirtualFile;
import com.antlr.plugin.ANTLRv4PluginController;
import com.antlr.plugin.psi.ParserRuleRefNode;
import org.jetbrains.annotations.NotNull;

public class TestRuleAction extends AnAction implements DumbAware {
    public static final Logger LOG = Logger.getInstance("ANTLR TestRuleAction");

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * Only show if selection is a grammar and in a rule
     */
    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setText("Test ANTLR Rule"); // default text
        presentation.setIcon(AllIcons.Actions.Execute);

        VirtualFile grammarFile = MyActionUtils.getGrammarFileFromEvent(e);
        if (grammarFile == null) { // we clicked somewhere outside text or non grammar file
            presentation.setEnabled(false);
            presentation.setVisible(false);
            return;
        }

        ParserRuleRefNode r = MyActionUtils.getParserRuleSurroundingRef(e);
        if (r == null) {
            presentation.setEnabled(false);
            return;
        }

        presentation.setVisible(true);
        String ruleName = r.getText();
        if (Character.isLowerCase(ruleName.charAt(0))) {
            presentation.setEnabled(true);
            presentation.setText("Test Rule " + ruleName);
        } else {
            presentation.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        if (e.getProject() == null) {
            LOG.error("actionPerformed no project for " + e);
            return; // whoa!
        }
        VirtualFile grammarFile = MyActionUtils.getGrammarFileFromEvent(e);
        if (grammarFile == null) return;

        LOG.info("actionPerformed " + grammarFile);

        ANTLRv4PluginController controller = ANTLRv4PluginController.getInstance(e.getProject());
        if (controller != null) {
            controller.showPre(null);
            controller.currentEditorFileChangedEvent(e.getProject(),null, grammarFile, false);
        }

        ParserRuleRefNode r = MyActionUtils.getParserRuleSurroundingRef(e);
        if (r == null) {
            return; // weird. no rule name.
        }
        String ruleName = r.getText();
        FileDocumentManager docMgr = FileDocumentManager.getInstance();
        Document doc = docMgr.getDocument(grammarFile);
        if (doc != null) {
            docMgr.saveDocument(doc);
        }
        if (controller != null) {
            controller.setStartRuleNameEvent(grammarFile, ruleName);
        }
    }

}
