package moe.quill.pinion.selections.managers.zones

import moe.quill.pinion.commands.translation.CommandArgTranslator
import moe.quill.pinion.core.config.ConfigManager
import moe.quill.pinion.selectapi.components.Zone
import org.bukkit.plugin.Plugin

//TODO: Maybe back these with hashmaps for quicker lookup? Dont think it matters.
class ZoneManager(plugin: Plugin) :
    ConfigManager<MutableList<Zone>>(plugin, { mutableListOf() }, "zones.yml"),
    CommandArgTranslator<Zone> {

    fun save(zone: Zone) {
        data += zone
        write()
    }

    fun remove(name: String) {
        data.removeIf { it.name == name }
        write()
    }

    fun get(name: String): Zone? {
        return data.firstOrNull { it.name == name }
    }

    override fun translationNames(): Collection<String> {
        return data.map { it.name }
    }

    override fun translateArgument(arg: String): Zone? {
        return get(arg)
    }

}