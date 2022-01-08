package moe.quill.pinion.spawners.commands

import com.comphenix.protocol.ProtocolManager
import moe.quill.pinion.commands.annotations.Command
import moe.quill.pinion.commands.annotations.CommandGroup
import moe.quill.pinion.core.menu.openMenu
import moe.quill.pinion.glow.GlowHandler
import moe.quill.pinion.spawners.lib.Spawner
import moe.quill.pinion.spawners.config.SpawnerManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

@CommandGroup("spawner", permission = "pinion.spanwers")
class SpawnerCommand(
    private val plugin: Plugin,
    private val glowHandler: GlowHandler,
    private val spawnerManager: SpawnerManager
) {

    @Command("create")
    fun create(sender: CommandSender, name: String) {
        if (sender !is Player) return
        spawnerManager.translateArgument(name)?.let {
            sender.sendMessage(Component.text("Spawner with the name $name already exists!").color(NamedTextColor.RED))
            return
        }

        val spawner = Spawner(plugin, name, sender.location.block)
        spawnerManager.addSpawner(spawner)
        sender.sendMessage(Component.text("Created a spawner with the name ${name}!").color(NamedTextColor.GREEN))
    }

    @Command("tp")
    fun teleport(player: CommandSender, spawner: Spawner) {
        if (player !is Player) return
        player.teleportAsync(spawner.block.location)
    }

    @Command("editmode")
    fun editMode(sender: CommandSender) {
        if (sender !is Player) return

        val value = spawnerManager.editorMode.compute(sender.uniqueId) { _, editing ->
            if (editing == null) true
            else !editing
        }

        sender.sendMessage(Component.text("Set edit mode to $value").color(NamedTextColor.GREEN))
    }

    @Command("save")
    fun save(sender: CommandSender) {
        spawnerManager.write()
        sender.sendMessage(Component.text("Saved spawner data."))
    }

    @Command("edit")
    fun edit(player: CommandSender, spawner: Spawner) {
        if (player !is Player) return
        player.openMenu(spawnerManager.createSpawnerGUI(spawner))
        player.sendMessage(Component.text("Opened the editor for the spawner '${spawner.name}'!"))
    }

    @Command("addType")
    fun addType(sender: CommandSender, spawner: Spawner, type: EntityType) {
        spawnerManager.addType(spawner.name, type)
        sender.sendMessage(Component.text("Added type ${type.name} to the spawner ${spawner.name}"))
    }

    private val glowState = mutableMapOf<UUID, Boolean>()

    @Command("highlight")
    fun highlight(sender: CommandSender) {
        if (sender !is Player) return
        //Map the values
        val glow = glowState.compute(sender.uniqueId) { _, value ->
            if (value == null) true
            else !value
        } ?: return

        if (glow) {
            spawnerManager.data.forEach { glowHandler.showGlow(sender, it.block) }
            return
        }

        spawnerManager.data.forEach { glowHandler.hideGlow(sender, it.block) }
    }
}