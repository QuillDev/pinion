package moe.quill.pinion.selections.managers.schematic

import moe.quill.pinion.commands.annotations.Command
import moe.quill.pinion.commands.annotations.CommandGroup
import moe.quill.pinion.core.characteristics.join
import moe.quill.pinion.selectapi.components.Bounds
import moe.quill.pinion.selectapi.components.Schematic
import moe.quill.pinion.selectapi.components.Zone
import moe.quill.pinion.selectapi.components.handler.SelectionHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandGroup("schem", aliases = ["schematic"])
class SchematicCommands(private val selectionHandler: SelectionHandler) {

    @Command("save")
    fun save(sender: CommandSender, name: String) {
        if (sender !is Player) return

        if (name.isEmpty()) {
            sender.sendMessage(Component.text("Invalid Name.").color(NamedTextColor.RED))
            return
        }

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

    @Command("remove", aliases = ["delete", "del"])
    fun remove(sender: CommandSender, schematic: Schematic) {
        selectionHandler.removeSchematic(schematic)
        sender.sendMessage(
            Component.text(
                "Removed the schematic '${schematic.name}'!"
            ).color(NamedTextColor.GREEN)
        )
        return
    }

    @Command("paste")
    fun paste(sender: CommandSender, schematic: Schematic) {
        if (sender !is Player) return

        sender.sendMessage(Component.text("Pasting schematic ${schematic.name}!").color(NamedTextColor.GREEN))

        val senderLocation = sender.location
        val location = senderLocation.clone()

        val blockData = schematic.blockData
        for (x in blockData.indices) {
            for (y in blockData[x].indices) {
                for (z in blockData[x][y].indices) {
                    location.clone().add(x.toDouble(), y.toDouble(), z.toDouble()).block.blockData = blockData[x][y][z]
                }
            }
        }
    }

    @Command("list")
    fun list(sender: CommandSender) {
        sender.sendMessage(selectionHandler.getSchematics()
            .join(Component.text(", ").color(NamedTextColor.DARK_GRAY)) {
                it.color(NamedTextColor.GREEN)
            })
    }
}