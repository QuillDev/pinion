package moe.quill.pinion.core.config

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.nio.file.Path

open class ConfigManager<T : Any>(
    plugin: Plugin,
    val default: () -> T,
    vararg pathExtension: String,
) {

    val path: Path
    var data: T

    init {
        this.path = Path.of(plugin.dataFolder.path.toString(), *pathExtension)
        this.data = read()
    }

    fun yaml(): YamlConfiguration {
        if (!path.toFile().exists()) {
            path.parent.toFile().mkdirs()
            path.toFile().createNewFile()
        }
        return YamlConfiguration.loadConfiguration(path.toFile())
    }

    fun reloadFromFile() {
        this.data = read()
    }

    open fun read(): T {
        val root = yaml().get("root") ?: return default()
        return root as T
    }

    fun write(data: T = this.data) {
        val raw = YamlConfiguration()
        raw.set("root", data)
        path.toFile().writeText(raw.saveToString())
    }
}