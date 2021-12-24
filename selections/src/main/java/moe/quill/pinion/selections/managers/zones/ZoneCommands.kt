package moe.quill.pinion.selections.managers.zones

import moe.quill.pinion.commands.annotations.Command
import moe.quill.pinion.commands.annotations.CommandGroup
import moe.quill.pinion.core.geometry.BoxUtil
import moe.quill.pinion.selectapi.components.Bounds
import moe.quill.pinion.selectapi.components.Zone
import moe.quill.pinion.selectapi.components.handler.SelectionHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandGroup("zone", aliases = ["zones"])
class ZoneCommands(private val selectionHandler: SelectionHandler) {

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
        selectionHandler.getZone(name)?.let {
            sender.sendMessage(
                Component.text(
                    "A zone with the name '$name' already exists."
                ).color(NamedTextColor.RED)
            )
            return
        }
        //Add the new location
        selectionHandler.addZone(
            Zone(
                name,
                Bounds(selection.first.toVector(), selection.second.toVector(), selection.first.world)
            )
        )
        sender.sendMessage(
            Component.text(
                "Saved a new zone with the name '$name'!"
            ).color(NamedTextColor.GREEN)
        )

    }

    @Command("highlight")
    fun highlight(zone: Zone) {
        BoxUtil.getPoints(zone.toBoundingBox()).forEach {
            zone.world.spawnParticle(
                Particle.REDSTONE,
                it.toLocation(zone.world),
                1,
                Particle.DustOptions(Color.RED, 4f)
            )
        }
    }

    @Command("remove", aliases = ["delete", "del"])
    fun remove(sender: CommandSender, zone: Zone) {
        selectionHandler.removeZone(zone)
        sender.sendMessage(
            Component.text(
                "Removed the zone '${zone.name}'!"
            ).color(NamedTextColor.GREEN)
        )
    }
}