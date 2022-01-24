package moe.quill.pinion.spawners.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import moe.quill.pinion.core.functional.Lambda
import moe.quill.pinion.glow.GlowHandler
import moe.quill.pinion.glow.glowCache
import org.bukkit.block.BlockFace
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin

class GlowPassthrough(
    plugin: Plugin
) : PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {

    override fun onPacketReceiving(event: PacketEvent?) {
        event ?: return

        val packet = event.packet
        val id = packet.integers.read(0) ?: return
        val block = glowCache[id] ?: return

        Lambda {
            plugin.server.pluginManager.callEvent(
                PlayerInteractEvent(
                    event.player,
                    Action.RIGHT_CLICK_BLOCK,
                    event.player.inventory.itemInMainHand,
                    block,
                    BlockFace.SELF
                )
            )
        }.runTask(plugin)

    }
}