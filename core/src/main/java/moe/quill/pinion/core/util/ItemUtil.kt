package moe.quill.pinion.core.util

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun ItemStack.hasKey(key: NamespacedKey): Boolean = hasKey(key, PersistentDataType.STRING)

fun <T, Z> ItemStack.hasKey(key: NamespacedKey, type: PersistentDataType<T, Z>) =
    itemMeta?.persistentDataContainer?.has(key, type) ?: false
