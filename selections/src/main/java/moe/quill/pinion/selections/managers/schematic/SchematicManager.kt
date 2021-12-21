package moe.quill.pinion.selections.managers.schematic

import moe.quill.pinion.commands.translation.CommandArgTranslator
import moe.quill.pinion.core.config.ConfigManager
import moe.quill.pinion.selectapi.components.Schematic
import org.bukkit.plugin.Plugin

//TODO: Maybe back these with hashmaps for quicker lookup? Dont think it matters.
class SchematicManager(plugin: Plugin) :
    ConfigManager<MutableList<Schematic>>(plugin, { mutableListOf() }, "schematics.yml"),
    CommandArgTranslator<Schematic> {

    fun save(schematic: Schematic) {
        data += schematic
        write()
    }

    fun remove(name: String) {
        data.removeIf { it.name == name }
        write()
    }

    fun get(name: String): Schematic? {
        return data.firstOrNull { it.name == name }
    }

    override fun translationNames(): Collection<String> {
        return data.map { it.name }
    }

    override fun translateArgument(arg: String): Schematic? {
        return get(arg)
    }

}