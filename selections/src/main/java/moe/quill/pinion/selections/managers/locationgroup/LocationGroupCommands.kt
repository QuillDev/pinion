package moe.quill.pinion.selections.managers.locationgroup

import moe.quill.pinion.commands.annotations.Command
import moe.quill.pinion.commands.annotations.CommandGroup
import moe.quill.pinion.core.extensions.registerEvents
import moe.quill.pinion.core.items.itemBuilder
import moe.quill.pinion.core.util.hasKey
import moe.quill.pinion.selectapi.components.LocationGroup
import moe.quill.pinion.selectapi.components.handler.SelectionHandler
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.plugin.Plugin
import java.util.*

@CommandGroup("lgroup", permission = "selections.lgroup")
class LocationGroupCommands(
    private val plugin: Plugin,
    private val selectionHandler: SelectionHandler
) : Listener {

    private val groupSelectKey = NamespacedKey(plugin, "group-select-key")
    private val placeItem = itemBuilder(Material.PURPUR_BLOCK) {
        name { Component.text("Location Selector") }
        markerKey { groupSelectKey }
    }

    private val editing = mutableListOf<UUID>()
    private val selected = mutableMapOf<UUID, MutableList<Block>>()

    init {
        plugin.registerEvents(this)
    }

    @Command("create")
    fun create(player: CommandSender) {
        if (player !is Player) return
        //If this player is currently editing, stop them
        if (editing.contains(player.uniqueId)) {
            player.sendMessage(Component.text("You're already creating a group! Do /lgroup cancel to stop"))
            return
        }

        editing += player.uniqueId
        selected += player.uniqueId to mutableListOf()

        player.inventory.addItem(placeItem)
        player.sendMessage(Component.text("Entered selection mode!"))
    }

    @Command("save")
    fun save(player: CommandSender, name: String) {
        if (player !is Player) return
        val blocks = selected[player.uniqueId]

        if (blocks == null || blocks.isEmpty()) {
            player.sendMessage(Component.text("You must select blocks in order to save!"))
            return
        }

        //Check if there's an existing location
        selectionHandler.getPossibleLocationGroup(name)?.let {
            player.sendMessage(Component.text("A group already exists with the name $name!"))
            return
        }

        selectionHandler.addLocationGroup(name, blocks.map { it.location }.toMutableList())
        player.sendMessage(Component.text("Created location group $name!"))

        editing -= player.uniqueId
        selected -= player.uniqueId
    }

    @Command("del", aliases = ["delete"])
    fun delete(player: CommandSender, locationGroup: LocationGroup) {
        selectionHandler.removeLocationGroup(locationGroup.name)
        player.sendMessage(Component.text("Removed the location group with the name ${locationGroup.name}"))
    }

    @Command("cancel")
    fun cancel(player: CommandSender) {
        if (player !is Player) return
        //If they're not editing
        if (!editing.contains(player.uniqueId)) {
            player.sendMessage(Component.text("You are not currently creating a location group!"))
            return
        }
        editing -= player.uniqueId
        selected -= player.uniqueId
    }

    @EventHandler
    fun onInteract(event: BlockPlaceEvent) {
        val id = event.player.uniqueId
        if (!editing.contains(id)) return
        if (!event.itemInHand.hasKey(groupSelectKey)) return

        val block = event.block
        //Append it to the list
        selected.compute(id) { _, list ->
            if (list == null) {
                return@compute mutableListOf(block)
            }
            list += block
            return@compute list
        }
        event.player.sendMessage(Component.text("Added block @ location ${block.x}, ${block.y}, ${block.z} "))
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val selected = selected[player.uniqueId] ?: return
        if (!selected.contains(block)) return
        selected -= block
        event.player.sendMessage(Component.text("Removed block from group @ ${block.x}, ${block.y}, ${block.z}!"))
    }
}