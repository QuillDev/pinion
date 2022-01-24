package moe.quill.pinion.selections.managers.locationgroup

import moe.quill.pinion.commands.translation.CommandArgTranslator
import moe.quill.pinion.core.config.ConfigManager
import moe.quill.pinion.core.extensions.log
import moe.quill.pinion.selectapi.components.LocationGroup
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import java.util.logging.Level

//TODO: Maybe back these with hashmaps for quicker lookup? Dont think it matters.
class LocationGroupHandler(plugin: Plugin) :
    ConfigManager<MutableList<LocationGroup>>(plugin, { mutableListOf() }, "locationsgroups.yml"),
    CommandArgTranslator<LocationGroup> {

    fun save(name: String, locations: MutableList<Location>) {
        data += LocationGroup(name, locations)
        write()
    }

    fun remove(name: String) {
        data.removeIf { it.name == name }
        write()
    }

    fun get(name: String): LocationGroup? {
        val result = data.firstOrNull { it.name == name }
        result ?: run { log("Could not find location group named $name. this could cause NPEs!", Level.WARNING) }
        return result
    }

    override fun translationNames(): Collection<String> {
        return data.map { it.name }
    }

    override fun translateArgument(arg: String): LocationGroup? {
        return data.firstOrNull { it.name == arg }
    }
}