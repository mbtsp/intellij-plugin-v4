package org.antlr.intellij.plugin.util;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import org.antlr.intellij.plugin.ANTLRv4PluginController;
import org.antlr.intellij.plugin.toolwindow.ConsoleToolWindow;

public class ConsoleUtils {
    public static void consolePrint(Project project, String msg, ConsoleViewContentType contentType) {
        if (project != null && !project.isDisposed()) {
            ANTLRv4PluginController.showLaterConsoleWindow(project, () -> {
                project.getMessageBus().syncPublisher(ConsoleToolWindow.TOPIC).print(msg, contentType);
            });
        }
    }
}
