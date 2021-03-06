package moe.quill.pinion.selectapi.components

import moe.quill.pinion.core.characteristics.Named
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs

@SerializableAs("NamedLocation")
class NamedLocation(override val name: String, val location: Location) : ConfigurationSerializable, Named {

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): NamedLocation {
            return NamedLocation(map["name"] as String, map["location"] as Location)
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("name" to name, "location" to location)
    }
}