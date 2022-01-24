package moe.quill.pinion.buildertools.commands

import moe.quill.pinion.buildertools.commands.trails.TrailData
import moe.quill.pinion.commands.annotations.Command
import moe.quill.pinion.commands.annotations.CommandGroup
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.data.Waterlogged
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.Plugin
import java.util.*

@CommandGroup("bt", "buildtools", "buildertools")
class BuildToolCommands(private val plugin: Plugin) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @Command("speed", aliases = ["spd"])
    fun speed(player: CommandSender, amount: Float) {
        if (player !is Player) return
        player.walkSpeed = amount.coerceAtLeast(-1f).coerceAtMost(1f)
    }

    @Command("fspeed", aliases = ["fspd"])
    fun fspeed(player: CommandSender, amount: Float) {
        if (player !is Player) return
        player.flySpeed = amount.coerceAtLeast(-1f).coerceAtMost(1f)
    }

    @Command("drain")
    fun drain(player: CommandSender, radius: Int) {
        if (player !is Player) return
        val block = player.location.block

        val pX = block.x
        val pY = block.y
        val pZ = block.z

        val cacheLoc = block.location.clone()
        ((pX - radius)..(pX + radius)).forEach { x ->
            ((pY - radius)..(pY + radius)).forEach { y ->
                ((pZ - radius)..(pZ + radius)).forEach { z ->
                    cacheLoc.x = x.toDouble()
                    cacheLoc.y = y.toDouble()
                    cacheLoc.z = z.toDouble()

                    val blockHere = cacheLoc.block
                    val data = blockHere.blockData
                    if (data is Waterlogged) {
                        data.isWaterlogged = false
                        blockHere.blockData = data
                    }
                    if (blockHere.type == Material.WATER) {
                        blockHere.type = Material.AIR
                    }
                }
            }
        }
    }

    val trailing = mutableMapOf<UUID, TrailData>()

    @Command("trail")
    fun trail(player: CommandSender, type: Material, thickness: Int) {
        if (player !is Player) return
        player.sendMessage(Component.text("Starting Trail with Type ${type.name}"))
        trailing += player.uniqueId to TrailData(type, thickness)
    }

    @Command("stoptrail")
    fun stopTrail(player: CommandSender) {
        if (player !is Player) return
        trailing -= player.uniqueId
        player.sendMessage(Component.text("Stopped Trail"))
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val trailData = trailing[event.player.uniqueId] ?: return
        if (event.to.toVector() == event.from.toVector()) return
        event.player.location.block.type = trailData.type
    }
}