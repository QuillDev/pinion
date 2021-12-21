package moe.quill.pinion.selections.components

import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs

@SerializableAs("NamedLocation")
class NamedLocation(val name: String, val location: Location) : ConfigurationSerializable {

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