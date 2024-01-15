package com.antlr

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId

object ApplicationInfo {
    @JvmField
    var VERSION = loadVersion()
    private fun loadVersion(): String? {
        val idePluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId("com.my.antlr.tool"))
            ?: return null
        return idePluginDescriptor.version
    }
}