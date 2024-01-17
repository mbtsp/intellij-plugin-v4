package com.antlr.notify

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project


fun notify(
    project: Project?,
    title: String,
    type: NotificationType,
    content: String,
    actions: Collection<AnAction>?
) {
    val notification = NotificationGroupManager.getInstance()
        .getNotificationGroup("antlr.new.notify.group")
        .createNotification(title, content, type)
    if (!actions.isNullOrEmpty()) {
        notification.addActions(actions)
    }
    notification.notify(project)
}

fun notifySuccess(project: Project?, title: String, content: String, actions: Collection<AnAction>?) {
    notify(project, title, NotificationType.INFORMATION, content, actions)
}

fun notifySuccess(project: Project?, title: String, content: String) {
    notify(project, title, NotificationType.INFORMATION, content, null)
}

fun notifyWarning(project: Project?, title: String, content: String) {
    notify(project, title, NotificationType.WARNING, content, null)
}

fun notifyConflictsWarning(project: Project?, content: String, actions: Collection<AnAction>?) {
    notifyWarning(project, "Plugin conflicts", content, actions)
}

fun notifyWarning(project: Project?, title: String, content: String, actions: Collection<AnAction>?) {
    notify(project, title, NotificationType.WARNING, content, actions)
}