package org.antlr.intellij.plugin.listeners;

import com.intellij.execution.ui.ConsoleViewContentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EventListener;

public interface ConsoleListener extends EventListener {
    void show(@Nullable Runnable runnable);

    void print(@NotNull String msg, @NotNull ConsoleViewContentType contentType);
}
