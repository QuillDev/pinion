package moe.quill.pinion.selections.managers.schematic

import moe.quill.pinion.commands.annotations.Command
import moe.quill.pinion.commands.annotations.CommandGroup
import moe.quill.pinion.selectapi.components.Bounds
import moe.quill.pinion.selectapi.components.Schematic
import moe.quill.pinion.selectapi.components.Zone
import moe.quill.pinion.selectapi.components.handler.SelectionHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandGroup("schem", aliases = ["schematic"])
class SchematicCommands(private val selectionHandler: SelectionHandler) {

    @Command("save")
    fun save(sender: CommandSender, name: String) {
        if (sender !is Player) return
        val selection = selectionHandler.getSelection(sender.uniqueId) ?: run {
            sender.sendMessage(
                Component.text(
                    "You do not have a zone selected!"
                ).color(NamedTextColor.RED)
            )
            return
        }
        //Get the location with the given name
        selectionHandler.getSchematic(name)?.let {
            sender.sendMessage(
                Component.text(
                    "A schematic with the name '$name' already exists."
                ).color(NamedTextColor.RED)
            )
            return
        }
        //Add the new location
        selectionHandler.addSchematic(
            Schematic(
                Zone(
                    name,
                    Bounds(selection.first.toVector(), selection.second.toVector(), selection.first.world)
                )
            )
        )
        sender.sendMessage(
            Component.text(
                "Saved a new schematic with the name '$name'!"
            ).color(NamedTextColor.GREEN)
        )

    }

    @Command("remove")
    fun remove(sender: CommandSender, schematic: Schematic) {
        selectionHandler.removeSchematic(schematic)
        sender.sendMessage(
            Component.text(
                "Removed the schematic '${schematic.name}'!"
            ).color(NamedTextColor.GREEN)
        )
        return
    }
}