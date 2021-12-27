package moe.quill.pinion.core.items

import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*

@Deprecated("Replaced in favor of using the normal itemBuilder method")
class ItemBuilder(private val item: ItemStack) {
    constructor(type: Material) : this(ItemStack(type))

    fun name(name: Component, italic: Boolean = false): ItemBuilder {
        return applyMeta { it.displayName(name.decoration(TextDecoration.ITALIC, italic)) }
    }

    //keys
    fun <T, Z> addKey(key: NamespacedKey, type: PersistentDataType<T, Z>, value: Z): ItemBuilder {
        return applyData { it.set(key, type, value!!) }
    }

    fun addMarkerKey(key: NamespacedKey): ItemBuilder {
        return addKey(key, PersistentDataType.STRING, "")
    }

    //lore
    fun appendLore(vararg lore: Component): ItemBuilder {
        return appendLore(lore.toList())
    }

    fun appendLore(lore: List<Component>): ItemBuilder {
        val old = lore()
        old += lore
        return lore(old)
    }

    fun lore(vararg lore: Component): ItemBuilder {
        return lore(lore.toList())
    }

    fun lore(lore: List<Component>): ItemBuilder {
        return applyMeta { it.lore(lore) }
    }

    fun lore(): MutableList<Component> {
        return meta()?.lore() ?: mutableListOf()
    }

    //Properties
    fun amount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    fun type(type: Material): ItemBuilder {
        item.type = type
        return this
    }

    fun unbreakable(unbreakable: Boolean = true): ItemBuilder {
        return applyMeta { it.isUnbreakable = unbreakable }
    }

    fun modelData(data: Int): ItemBuilder {
        return applyMeta { it.setCustomModelData(data) }
    }

    fun flags(vararg flags: ItemFlag): ItemBuilder {
        return applyMeta { it.removeItemFlags(*flags) }
    }

    //Skull Meta
    fun setTexture(texture: String): ItemBuilder {
        return applyMeta {
            val skull = it as? SkullMeta ?: return@applyMeta
            val profile = Bukkit.createProfile(UUID.randomUUID())
            profile.setProperty(ProfileProperty("textures", texture))
            skull.playerProfile = profile
        }
    }

    fun setOwner(offlinePlayer: OfflinePlayer): ItemBuilder {
        return applyMeta {
            val skull = it as? SkullMeta ?: return@applyMeta
            skull.owningPlayer = offlinePlayer
        }
    }

    //Meta
    fun meta(): ItemMeta? {
        return item.itemMeta
    }

    fun meta(meta: ItemMeta): ItemBuilder {
        item.itemMeta = meta
        return this
    }

    //Apply Changes
    fun applyData(consumer: (PersistentDataContainer) -> Unit): ItemBuilder {
        return applyMeta { consumer(it.persistentDataContainer) }
    }

    fun applyMeta(consumer: (ItemMeta) -> Unit): ItemBuilder {
        val meta = meta() ?: return this
        consumer(meta)
        return meta(meta)
    }

    fun build(): ItemStack {
        return item
    }
}