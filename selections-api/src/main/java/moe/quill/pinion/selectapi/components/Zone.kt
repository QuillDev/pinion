package moe.quill.pinion.selectapi.components

import moe.quill.pinion.core.characteristics.Named
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs

@SerializableAs("Zone")
open class Zone(override val name: String, bounds: Bounds) : Bounds(bounds), ConfigurationSerializable, Named {
    constructor(zone: Zone) : this(zone.name, Bounds(zone.min, zone.max, zone.world))

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): Bounds {
            return Zone(map["name"] as String, map["bounds"] as Bounds)
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("name" to name, "bounds" to Bounds(min, max, world))
    }
}