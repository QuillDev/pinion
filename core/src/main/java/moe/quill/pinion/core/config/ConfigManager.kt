package moe.quill.pinion.core.config

import moe.quill.pinion.core.extensions.log
import moe.quill.pinion.core.extensions.registerEvents
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import java.nio.file.Path

open class ConfigManager<T : Any>(
    open val plugin: Plugin,
    val default: () -> T,
    vararg pathExtension: String,
) : Listener {

    val path: Path
    var data: T

    init {
        this.path = Path.of(plugin.dataFolder.path.toString(), *pathExtension)
        this.data = read()

        setupSave()
    }

    fun yaml(): YamlConfiguration {
        if (!path.toFile().exists()) {
            log("Config file was absent, saving to $path")
            write(default())
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

        val file = path.toFile()
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.writeText(raw.saveToString())
    }

    private fun setupSave() {
        plugin.registerEvents(this)
    }

    @EventHandler
    fun saveOnDisable(event: PluginDisableEvent) {
        log("Saving config ${this::class.simpleName}")
        write()
    }
}