package org.antlr.intellij.plugin.init;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.startup.StartupActivity;
import org.antlr.intellij.plugin.ANTLRv4PluginController;
import org.jetbrains.annotations.NotNull;

public class AntlrStartupActivity implements StartupActivity, DumbAware {
    private final static Logger log = Logger.getInstance(AntlrStartupActivity.class);

    @Override
    public void runActivity(@NotNull Project project) {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
            @Override
            public void projectClosing(@NotNull Project project) {
                log.info("Project " + project.getName() + " is closing");
                ANTLRv4PluginController antlRv4PluginController = ANTLRv4PluginController.getInstance(project);
                if (antlRv4PluginController != null) {
                    antlRv4PluginController.projectClosed();
                }
            }
        });

    }
}
