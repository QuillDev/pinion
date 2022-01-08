package moe.quill.pinion.spawners.lib

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

@SerializableAs("EntityMeta")
class EntityMeta(
    var type: EntityType,
    var name: String = type.name,
    var invisible: Boolean = false,
    var invulnerable: Boolean = false,
    var glowing: Boolean = false,
    var charged: Boolean = false,
    var size: Int = 1,
    //Equipment
    var helmet: ItemStack? = null,
    var chest: ItemStack? = null,
    var leggings: ItemStack? = null,
    var boots: ItemStack? = null,
    var mainHand: ItemStack? = null,
    var offHand: ItemStack? = null
) : ConfigurationSerializable {

    override fun serialize(): MutableMap<String, Any?> {
        return mutableMapOf(
            "type" to type.name,
            "name" to name,
            "invisible" to invisible,
            "invulnerable" to invulnerable,
            "glowing" to glowing,
            "charged" to charged,
            "size" to size,
            //Equipment
            "helmet" to helmet,
            "chest" to chest,
            "leggings" to leggings,
            "boots" to boots,
            "mainHand" to mainHand,
            "offHand" to offHand
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): EntityMeta {
            return EntityMeta(
                enumValueOf(map["type"] as String),
                map["name"] as? String ?: "Name",
                map["invisible"] as? Boolean ?: false,
                map["invulnerable"] as? Boolean ?: false,
                map["glowing"] as? Boolean ?: false,
                map["charged"] as? Boolean ?: false,
                map["size"] as? Int ?: 1,
                //Equipment
                map["helmet"] as? ItemStack,
                map["chest"] as? ItemStack,
                map["leggings"] as? ItemStack,
                map["boots"] as? ItemStack,
                map["mainHand"] as? ItemStack,
                map["offHand"] as? ItemStack
            )
        }
    }

}