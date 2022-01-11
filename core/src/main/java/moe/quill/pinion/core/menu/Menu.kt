package moe.quill.pinion.core.menu

import moe.quill.pinion.core.extensions.log
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.Plugin
import java.util.logging.Level

fun Player.openMenu(menu: Menu) = menu.show(this)

open class Menu(
    private val plugin: Plugin,
    private val title: Component,
    private val rows: Int
) : Listener {

    private val contents = mutableMapOf<Int, MenuItem>()
    val size = (rows * 9).coerceAtMost(54).coerceAtLeast(9)

    val inventory = Bukkit.createInventory(
        null,
        size,
        title
    )

    init {
        register()
    }

    private fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    /**
     * Set the item at the given slot to the given menu item,
     * if a null value is supplied
     * then we set the slot to be nothing.
     */
    fun set(slot: Int, itemSupplier: (Int) -> MenuItem?) {
        itemSupplier(slot)?.let {
            contents[slot] = it
            inventory.setItem(slot, it.icon)
        } ?: run {
            contents -= slot
            inventory.setItem(slot, null)
        }
    }

    fun append(itemSupplier: (Int) -> MenuItem) {
        firstOpenSlot()?.let { set(it) { itemSupplier(it) } }
    }

    fun firstOpenSlot(): Int? {
        for (slot in 0 until size) {
            if (contents[slot] != null) continue
            return slot
        }

        log("No open slots in the menu!", Level.WARNING)
        return null
    }

    fun show(player: Player) {
        player.openInventory(inventory)
    }

    fun copy(): Menu {
        val clone = Menu(plugin, title, rows)

        contents.forEach { (idx, item) ->
            clone.set(idx) { item }
        }

        return clone
    }

    fun reloadInventory(clear: Boolean = false) {
        if (clear) inventory.clear()
        contents.forEach { (idx, item) ->
            inventory.setItem(idx, item.icon)
        }
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory != inventory) return
        val item = contents[event.rawSlot] ?: return
        if (item.mutable) return
        val player = event.whoClicked as? Player ?: return
        item.action.invoke(player)
        event.isCancelled = true
    }
}

fun menuBuilder(
    plugin: Plugin,
    title: Component = Component.text("Menu"),
    rows: Int = 3,
    init: Menu .() -> Unit
): Menu {
    val menu = Menu(plugin, title, rows)
    menu.init()
    return menu
}