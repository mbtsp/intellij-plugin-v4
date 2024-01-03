package org.antlr.intellij.plugin.toolwindow;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.Topic;
import org.antlr.intellij.plugin.Icons;
import org.antlr.intellij.plugin.listeners.ConsoleListener;
import org.jetbrains.annotations.NotNull;

public class ConsoleToolWindow implements ToolWindowFactory {
    private ConsoleView console;
    public static final Topic<ConsoleListener> TOPIC = new Topic<>(ConsoleListener.class);


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
        TextConsoleBuilder consoleBuilder = factory.createBuilder(project);
        this.console = consoleBuilder.getConsole();
        Content content = ContentFactory.getInstance().createContent(console.getComponent(), "", false);
        content.setCloseable(false);
        content.setDisposer(console);
        toolWindow.getContentManager().addContent(content);

    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(Icons.getToolWindow());
        Project project = toolWindow.getProject();
        if (!project.isDisposed()) {
            project.getMessageBus().connect().subscribe(TOPIC, (ConsoleListener) (msg, contentType) -> {
                if (console != null) {
                    console.print(msg, contentType);
                }
            });
        }
    }

    public ConsoleView getConsole() {
        return this.console;
    }
}
