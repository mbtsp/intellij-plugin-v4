package com.antlr.plugin.util;

import com.antlr.plugin.toolwindow.ConsoleToolWindow;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.antlr.plugin.ANTLRv4PluginController;

public class ConsoleUtils {
    public static void consolePrint(Project project, String msg, ConsoleViewContentType contentType) {
        if (project != null && !project.isDisposed()) {
            ANTLRv4PluginController.showLaterConsoleWindow(project, () -> {
                project.getMessageBus().syncPublisher(ConsoleToolWindow.TOPIC).print(msg, contentType);
            });
        }
    }
}
