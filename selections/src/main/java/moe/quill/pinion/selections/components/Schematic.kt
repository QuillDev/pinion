package moe.quill.pinion.selections.components

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs

@SerializableAs("Schematic")
class Schematic(zone: Zone, val blockData: List<List<List<BlockData>>>) : Zone(zone), ConfigurationSerializable {
    constructor(zone: Zone) : this(zone, getBlockData(zone))

    companion object {
        @JvmStatic
        fun getBlockData(bounds: Bounds): List<List<List<BlockData>>> {
            val xAxis = mutableListOf<List<List<BlockData>>>()
            for (x in bounds.min.x.toInt() until bounds.max.x.toInt()) {
                val yAxis = mutableListOf<List<BlockData>>()
                for (y in bounds.min.y.toInt() until bounds.max.y.toInt()) {
                    val zAxis = mutableListOf<BlockData>()
                    for (z in bounds.min.z.toInt() until bounds.max.z.toInt()) {
                        val position =
                            Location(bounds.world, x.toDouble(), y.toDouble(), z.toDouble()).toBlockLocation()
                        zAxis += position.block.blockData
                    }
                    yAxis += zAxis
                }
                xAxis += yAxis
            }

            return xAxis.toList()
        }

        @JvmStatic
        fun blockDataToString(data: List<List<List<BlockData>>>): List<List<List<String>>> {
            return data.map { yAxis -> yAxis.map { zAxis -> zAxis.map { datum -> datum.asString } } }
        }

        @JvmStatic
        fun stringToBlockData(data: List<List<List<String>>>): List<List<List<BlockData>>> {
            return data.map { yAxis -> yAxis.map { zAxis -> zAxis.map { datum -> Bukkit.createBlockData(datum) } } }
        }

        @JvmStatic
        fun deserialize(map: Map<String, Any>): Schematic {
            return Schematic(map["zone"] as Zone, stringToBlockData(map["data"] as List<List<List<String>>>))
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("zone" to Zone, "data" to blockDataToString(blockData))
    }
}