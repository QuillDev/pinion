package moe.quill.pinion.core.items

import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionData
import java.util.*

//Extension for converting an itemstack to a builder
fun ItemStack.builder(init: ItemBuilder.() -> Unit) = itemBuilder(this, init)

class ItemBuilder(private val item: ItemStack) {
    constructor(type: Material) : this(ItemStack(type))

    private val lore = Lore(this)

    fun applyMeta(applier: (ItemMeta) -> Unit) {
        item.itemMeta?.let { applier(it); item.itemMeta = it }
    }

    fun skullTexture(texture: () -> String) {
        applyMeta {
            val skull = it as? SkullMeta ?: return@applyMeta
            val profile = Bukkit.createProfile(UUID.randomUUID())
            profile.setProperty(ProfileProperty("textures", texture()))
            skull.playerProfile = profile
        }
    }

    fun skullOwner(player: () -> OfflinePlayer) {
        applyMeta {
            val skull = it as? SkullMeta ?: return@applyMeta
            skull.owningPlayer = player()
        }
    }

    fun lore(mutator: Lore.() -> Unit) {
        lore.mutator()
    }

    fun <T, Z> addKey(key: NamespacedKey, type: PersistentDataType<T, Z>, value: Z) {
        applyMeta { it.persistentDataContainer.set(key, type, value!!) }
    }

    fun markerKey(key: () -> NamespacedKey) {
        addKey(key(), PersistentDataType.STRING, "")
    }

    fun name(supplier: () -> Component) {
        applyMeta { it.displayName(supplier()) }
    }

    fun potionData(data: () -> PotionData) {
        applyMeta {
            val meta = it as? PotionMeta ?: return@applyMeta
            meta.basePotionData = data()
        }
    }

    fun amount(amount: () -> Int) {
        item.amount = amount()
    }

    fun unbreakable(unbreakable: () -> Boolean) {
        applyMeta { it.isUnbreakable = unbreakable() }
    }

    fun modelData(modelData: () -> Int?) {
        applyMeta { it.setCustomModelData(modelData()) }
    }

    fun build(): ItemStack {
        return item
    }
}

fun itemBuilder(type: Material, init: ItemBuilder.() -> Unit = {}): ItemStack {
    return itemBuilder(ItemStack(type), init)
}

fun itemBuilder(item: ItemStack, init: ItemBuilder.() -> Unit = {}): ItemStack {
    val ib = ItemBuilder(item)
    ib.init()
    return ib.build()
}


