package org.antlr.intellij.plugin.toolwindow;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.Topic;
import org.antlr.intellij.plugin.Icons;
import org.antlr.intellij.plugin.listeners.ConsoleListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if (!project.isDisposed()) {
            project.getMessageBus().connect().subscribe(TOPIC, new ConsoleListener() {
                @Override
                public void show(@Nullable Runnable runnable) {
                    toolWindow.show(runnable);
                }

                @Override
                public void print(@NotNull String msg, @NotNull ConsoleViewContentType contentType) {
                    console.print(msg, contentType);
                }
            });
        }
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(Icons.getToolWindow());
    }

    public ConsoleView getConsole() {
        return this.console;
    }
}
