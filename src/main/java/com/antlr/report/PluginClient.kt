package com.antlr.report

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginDescriptor

object PluginClient {
    fun collectPlugin(): List<PluginDescriptor> {
        val plugins: Array<out IdeaPluginDescriptor> = PluginManager.getPlugins()
        if (plugins.isEmpty()) return listOf();
        val result = mutableListOf<PluginDescriptor>()
        plugins.forEach {
            if (!it.isBundled) {
                result.add(it)
            }
        }
        return result
    }
}
