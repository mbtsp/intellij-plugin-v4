package com.antlr

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId

object ApplicationInfo {
    @JvmField
    var PLUGIN_ID = "com.my.antlr.tool";
    @JvmField
    var VERSION = loadVersion()
    private fun loadVersion(): String? {
        val idePluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))
            ?: return null
        return idePluginDescriptor.version
    }
}