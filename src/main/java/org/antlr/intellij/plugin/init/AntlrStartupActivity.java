package org.antlr.intellij.plugin.init;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.startup.StartupActivity;
import org.antlr.intellij.notify.NotifyClientKt;
import org.antlr.intellij.plugin.ANTLRv4PluginController;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class AntlrStartupActivity implements StartupActivity, DumbAware {
    private final static Logger log = Logger.getInstance(AntlrStartupActivity.class);
    private static final List<String> plugins = List.of("org.antlr.intellij.plugin");

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

        new Task.Backgroundable(project, "Detect plugins that may cause conflicts") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                boolean flag = false;
                StringBuilder stringBuilder = new StringBuilder();
                for (String id : plugins) {
                    PluginId pluginId = PluginId.getId(id);
                    IdeaPluginDescriptor ideaPluginDescriptor = PluginManagerCore.getPlugin(pluginId);
                    if (ideaPluginDescriptor != null && ideaPluginDescriptor.isEnabled()) {
                        stringBuilder.append(ideaPluginDescriptor.getName());
                        stringBuilder.append(",");
                        flag = true;
                    }
                }
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
                }
                if (flag) {
                    NotifyClientKt.notifyConflictsWarning(project, "<html>The plug-ins are below [" + stringBuilder + "] These plug-ins may conflict, please uninstall or disable for maximum experience</html>", Arrays.asList(new DisablePluginAction("Disable plugin"), new DisableAndRestartPluginAction("Disable the plugin and restart")));
                }
            }
        }.queue();


    }

    private static class DisablePluginAction extends NotificationAction {

        public DisablePluginAction(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
            disablePlugin(notification);
        }
    }

    private static void disablePlugin(@NotNull Notification notification) {
        for (String id : plugins) {
            PluginManager.disablePlugin(id);
        }
        notification.expire();
    }

    private static class DisableAndRestartPluginAction extends NotificationAction {

        public DisableAndRestartPluginAction(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
            disablePlugin(notification);
            ApplicationManager.getApplication().exit(true, false, true);
        }
    }
}
