package org.antlr.intellij.plugin.listeners;

import com.intellij.execution.ui.ConsoleViewContentType;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

public interface ConsoleListener extends EventListener {

    void print(@NotNull String msg, @NotNull ConsoleViewContentType contentType);
}
