package moe.quill.pinion.selections.managers.locations

import moe.quill.pinion.commands.annotations.CommandGroup
import moe.quill.pinion.commands.translation.CommandArgTranslator
import moe.quill.pinion.core.config.ConfigManager
import moe.quill.pinion.core.extensions.log
import moe.quill.pinion.selectapi.components.NamedLocation
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import java.util.logging.Level

//TODO: Maybe back these with hashmaps for quicker lookup? Dont think it matters.
@CommandGroup("loc", aliases = ["location"])
class LocationManager(plugin: Plugin) :
    ConfigManager<MutableList<NamedLocation>>(plugin, { mutableListOf() }, "locations.yml"),
    CommandArgTranslator<NamedLocation> {

    fun save(name: String, location: Location) {
        data += NamedLocation(name, location)
        write()
    }

    fun remove(name: String) {
        data.removeIf { it.name == name }
        write()
    }

    fun get(name: String): Location? {
        val result = data.firstOrNull { it.name == name }
        result ?: run { log("Could not find location named $name. this could cause NPEs!", Level.WARNING) }
        return result?.location
    }

    override fun translationNames(): Collection<String> {
        return data.map { it.name }
    }

    override fun translateArgument(arg: String): NamedLocation? {
        return data.firstOrNull { it.name == arg }
    }
}