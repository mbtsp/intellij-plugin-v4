package com.antlr.language.psrsing;

import com.antlr.listener.AntlrListener;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import org.antlr.v4.Tool;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.ANTLRToolListener;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

public class RunAntlrListener implements ANTLRToolListener {
    public final List<String> all = new ArrayList<>();
    public Tool tool;
    private final Project project;
    public boolean hasOutput = false;

    public RunAntlrListener(Tool tool, Project project) {
        this.tool = tool;

        this.project = project;
    }

    @Override
    public void info(String msg) {
        if (tool.errMgr.formatWantsSingleLineMessage()) {
            msg = msg.replace('\n', ' ');
        }
        if (!this.project.isDisposed()) {
            this.project.getMessageBus().syncPublisher(AntlrListener.Companion.getTOPIC()).print(msg + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        }
        hasOutput = true;
    }

    @Override
    public void error(ANTLRMessage msg) {
        track(msg, ConsoleViewContentType.ERROR_OUTPUT);
    }

    @Override
    public void warning(ANTLRMessage msg) {
        track(msg, ConsoleViewContentType.NORMAL_OUTPUT);
    }

    private void track(ANTLRMessage msg, ConsoleViewContentType errType) {
        ST msgST = tool.errMgr.getMessageTemplate(msg);
        String outputMsg = msgST.render();
        if (tool.errMgr.formatWantsSingleLineMessage()) {
            outputMsg = outputMsg.replace('\n', ' ');
        }
        if (!this.project.isDisposed()) {
            this.project.getMessageBus().syncPublisher(AntlrListener.Companion.getTOPIC()).print(outputMsg + "\n", errType);
        }
        hasOutput = true;
    }
}
