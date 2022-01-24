package moe.quill.pinion.selectapi.components

import moe.quill.pinion.core.characteristics.Named
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs

@SerializableAs("LocationGroup")
class LocationGroup(override val name: String, val locations: MutableList<Location>) : ConfigurationSerializable,
    Named {


    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): LocationGroup {
            return LocationGroup(
                map["name"] as String,
                map["locations"] as? MutableList<Location> ?: mutableListOf()
            )
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "name" to name,
            "locations" to locations
        )
    }
}