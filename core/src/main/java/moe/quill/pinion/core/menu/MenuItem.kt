package moe.quill.pinion.core.menu

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class MenuItem(val icon: ItemStack, val mutable: Boolean = false, val action: (Player) -> Unit = {}) {
    fun copy(): MenuItem {
        return MenuItem(icon.clone(), mutable, action)
    }
}