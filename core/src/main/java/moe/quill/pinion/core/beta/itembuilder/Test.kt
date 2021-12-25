package moe.quill.pinion.core.beta.itembuilder

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Test {

    init {
        val test = ItemStack(Material.TRIDENT)

        test.builder {
            name { Component.text("Hello World!") }
            lore { set(Component.text("")) }
        }
    }
}