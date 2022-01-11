package moe.quill.pinion.selections.managers.locations

import moe.quill.pinion.commands.annotations.Command
import moe.quill.pinion.commands.annotations.CommandGroup
import moe.quill.pinion.core.characteristics.join
import moe.quill.pinion.selectapi.components.NamedLocation
import moe.quill.pinion.selectapi.components.handler.SelectionHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandGroup("loc", aliases = ["location"])
class LocationCommands(private val selectionHandler: SelectionHandler) {

    @Command("save")
    fun save(sender: CommandSender, name: String) {
        if (sender !is Player) return

        if (name.isEmpty()) {
            sender.sendMessage(Component.text("Invalid Name.").color(NamedTextColor.RED))
            return
        }

        val loc = selectionHandler.getLeftSelection(sender.uniqueId) ?: run {
            sender.sendMessage(
                Component.text(
                    "No Location selected."
                ).color(NamedTextColor.RED)
            )
            return
        }
        //Get the location with the given name
        selectionHandler.getPossibleLocation(name)?.let {
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

    @Command("remove", aliases = ["delete", "del"])
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

    @Command("list")
    fun list(sender: CommandSender) {
        sender.sendMessage(selectionHandler.getLocations().join(Component.text(",")))
    }
}