package moe.quill.pinion.selections.managers.locations

import moe.quill.pinion.commands.annotations.Command
import moe.quill.pinion.commands.annotations.CommandGroup
import moe.quill.pinion.selections.components.NamedLocation
import moe.quill.pinion.selections.handler.SelectionHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandGroup("loc", aliases = ["location"])
class LocationCommands(private val selectionHandler: SelectionHandler) {

    @Command("save")
    fun save(sender: CommandSender, name: String) {
        if (sender !is Player) return
        val loc = selectionHandler.getLeftSelection(sender.uniqueId) ?: run {
            sender.sendMessage(
                Component.text(
                    "No Location selected."
                ).color(NamedTextColor.RED)
            )
            return
        }
        //Get the location with the given name
        selectionHandler.getLocation(name)?.let {
            sender.sendMessage(
                Component.text(
                    "A location with the name '$name' already exists."
                ).color(NamedTextColor.RED)
            )
            return
        }
        //Add the new location
        selectionHandler.addLocation(name, loc)
        sender.sendMessage(
            Component.text(
                "Saved a new location with the name '$name'!"
            ).color(NamedTextColor.GREEN)
        )

    }

    @Command("remove")
    fun remove(sender: CommandSender, namedLoc: NamedLocation) {
        selectionHandler.removeLocation(namedLoc)
        sender.sendMessage(
            Component.text(
                "Removed the location '${namedLoc.name}'!"
            ).color(NamedTextColor.GREEN)
        )
    }

    @Command("teleport", aliases = ["tp"])
    fun teleport(sender: CommandSender, namedLoc: NamedLocation) {
        if (sender !is Player) return
        sender.teleportAsync(namedLoc.location)
        sender.sendMessage(
            Component.text(
                "Warping you to '${namedLoc.name}'"
            ).color(NamedTextColor.GREEN)
        )
    }
}