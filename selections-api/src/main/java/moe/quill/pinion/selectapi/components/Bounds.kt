package moe.quill.pinion.selectapi.components

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs
import org.bukkit.entity.Entity
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

@SerializableAs("Bounds")
open class Bounds(min: Vector, max: Vector, val world: World) : ConfigurationSerializable {
    constructor(bounds: Bounds) : this(bounds.min, bounds.max, bounds.world)

    var min: Vector
        private set
    var max: Vector
        private set

    init {
        this.min = Vector.getMinimum(min, max)
        this.max = Vector.getMaximum(min, max)
    }

    //Various methods for checking of the entity is within the bounds of this zone
    fun inAABB(position: Vector): Boolean {
        return position.isInAABB(min, max)
    }

    fun inBounds(position: Vector): Boolean {
        return (min.blockX <= position.blockX && position.blockX <= max.blockX)
                && (min.blockY <= position.blockY && position.blockY <= max.blockY)
                && (min.blockZ <= position.blockZ && position.blockZ <= max.blockZ)
    }

    fun inBounds(position: Location): Boolean {
        return (position.world == world) && inBounds(position.toVector())
    }

    fun inAABB(position: Location): Boolean {
        return inAABB(position.toVector())
    }

    fun inAABB(entity: Entity): Boolean {
        return inAABB(entity.location)
    }

    fun blocks(): List<Block> {
        val blocks = mutableListOf<Block>()

        val location = Location(world, 0.0, 0.0, 0.0)
        for (x in min.x.toInt()..max.x.toInt()) {
            for (y in min.y.toInt()..max.y.toInt()) {
                for (z in min.z.toInt()..max.z.toInt()) {
                    location.x = x.toDouble()
                    location.y = y.toDouble()
                    location.z = z.toDouble()
                    blocks += location.block
                }
            }
        }

        return blocks
    }

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): Bounds {
            return Bounds(
                map["min"] as Vector,
                map["max"] as Vector,
                Bukkit.getWorld(map["world"] as String)!!
            )
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "min" to min,
            "max" to max,
            "world" to world.name
        )
    }

    fun toBoundingBox(): BoundingBox {
        return BoundingBox.of(min, max)
    }
}