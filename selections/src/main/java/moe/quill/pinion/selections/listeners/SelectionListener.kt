package moe.quill.pinion.selections.listeners

import moe.quill.pinion.selectapi.components.handler.SelectionHandler
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType

class SelectionListener(private val toolKey: NamespacedKey, private val selectionHandler: SelectionHandler) : Listener {

    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (event.item?.itemMeta?.persistentDataContainer?.has(toolKey, PersistentDataType.STRING) != true) return
        val location = event.clickedBlock?.location?.toBlockLocation() ?: return
        event.isCancelled = true

        val player = event.player
        val uuid = player.uniqueId
        when (event.action) {
            Action.LEFT_CLICK_BLOCK -> {
                selectionHandler.setLeftSelection(uuid, location)
                player.sendMessage(Component.text("Updated left selection -> world: ${location.world.name} x:${location.x}, y: ${location.y}, z: ${location.z}"))
            }
            Action.RIGHT_CLICK_BLOCK -> {
                selectionHandler.setRightSelection(uuid, location)
                player.sendMessage(Component.text("Updated right selection -> world: ${location.world.name} x:${location.x}, y: ${location.y}, z: ${location.z}"))

            }
            else -> {}
        }

        val left = selectionHandler.getLeftSelection(uuid) ?: return
        val right = selectionHandler.getRightSelection(uuid) ?: return
        selectionHandler.setSelection(uuid, left to right)
    }
}