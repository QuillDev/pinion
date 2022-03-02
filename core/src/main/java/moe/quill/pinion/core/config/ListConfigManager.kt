package moe.quill.pinion.core.config

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.plugin.Plugin

abstract class ListConfigManager<T : List<ConfigurationSerializable>>(
    plugin: Plugin,
    default: () -> T,
    vararg pathExtension: String,
) : ConfigManager<T>(plugin, default, *pathExtension) {

    abstract fun add(entry: T): Boolean
    abstract fun remove(entry: T): Boolean
    abstract fun remove(identifier: String): Boolean
    abstract fun get(identifier: String): T?
}