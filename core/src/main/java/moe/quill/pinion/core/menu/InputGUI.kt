package moe.quill.pinion.core.menu

import moe.quill.pinion.core.items.builder
import moe.quill.pinion.core.items.itemBuilder
import moe.quill.pinion.core.menu.icons.IconTexture
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

//Input for boolean inputs
fun booleanInput(
    idx: Int,
    parent: Menu,
    identifier: Component,
    base: Boolean,
    action: (Boolean) -> Unit,
    trueStack: ItemStack = itemBuilder(Material.GREEN_WOOL),
    falseStack: ItemStack = itemBuilder(Material.RED_WOOL),
): MenuItem {
    val booleanText =
        if (base) Component.text("True").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
        else Component.text("False").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)

    return MenuItem((if (base) trueStack.clone() else falseStack.clone()).builder {
        name { identifier }
        lore { +booleanText }
    }) {
        parent.set(idx) { booleanInput(idx, parent, identifier, !base, action, trueStack, falseStack) }
        action(!base)
    }
}

//Create a number supplier GUI
fun numberInput(
    plugin: Plugin,
    baseValue: Int,
    action: (Int) -> Unit,
    parent: () -> Menu? = { null },
    min: Int? = null,
    max: Int? = null
): Menu {
    var value = baseValue

    return menuBuilder(plugin, Component.text("Currently Set Value: $baseValue"), 2) {

        fun updateCounter() {
            set(4) {
                MenuItem(itemBuilder(Material.PLAYER_HEAD) {
                    skullTexture { IconTexture.QUESTION_MARK.texture }
                    name { Component.text(value) }
                })
            }
            set(13) {
                MenuItem(itemBuilder(Material.PLAYER_HEAD) {
                    skullTexture { IconTexture.CHECKMARK.texture }
                    name {
                        Component.text("Confirm: ").color(NamedTextColor.GREEN)
                            .append(Component.text(value).color(NamedTextColor.YELLOW))
                            .append(Component.text("?").color(NamedTextColor.GREEN))
                    }
                }) { player ->
                    parent()?.let { player.openMenu(it) }
                    action(value)
                }
            }

        }

        fun modifyAmount(amount: Int) = run {
            var updated = value + amount
            min?.let { updated = updated.coerceAtLeast(min) }
            max?.let { updated = updated.coerceAtMost(max) }
            value = updated
            updateCounter()
        }

        fun createIcon(amount: Int): MenuItem {
            val negative = amount < 0
            val color = if (negative) NamedTextColor.RED else NamedTextColor.GREEN
            val icon = if (negative) Material.REDSTONE_BLOCK else Material.EMERALD_BLOCK

            val text = if (negative) "$amount" else "+$amount"

            return MenuItem(itemBuilder(icon) {
                name {
                    Component.text(text).color(color)
                }
            }) { modifyAmount(amount) }
        }

        //Minus
        set(1) { createIcon(-10) }
        set(2) { createIcon(-5) }
        set(3) { createIcon(-1) }
        updateCounter()
        //Add
        set(5) { createIcon(1) }
        set(6) { createIcon(5) }
        set(7) { createIcon(10) }
    }
}

fun backButton(parent: () -> Menu): MenuItem = run {
    return@run MenuItem(itemBuilder(Material.PLAYER_HEAD) {
        skullTexture { IconTexture.BACK_ARROW.texture }
        name { Component.text("Back").color(NamedTextColor.RED).decorate(TextDecoration.BOLD) }
    }) {
        it.openMenu(parent())
    }
}