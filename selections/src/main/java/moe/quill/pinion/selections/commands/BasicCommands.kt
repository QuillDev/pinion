package moe.quill.pinion.selections.commands

import moe.quill.pinion.commands.annotations.Command
import moe.quill.pinion.commands.annotations.CommandGroup
import moe.quill.pinion.core.items.ItemBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandGroup("select")
class BasicCommands(private val toolKey: NamespacedKey) {
    @Command("tool", "Gives a selection tool.", aliases = ["wand"])
    fun tool(sender: CommandSender) {
        if (sender !is Player) return
        sender.inventory.addItem(
            ItemBuilder(Material.STICK)
                .name(Component.text("Selection Tool").color(NamedTextColor.GREEN))
                .appendLore(Component.text("Quillstick").color(NamedTextColor.AQUA))
                .addMarkerKey(toolKey)
                .build()
        )
    }
}