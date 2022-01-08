package moe.quill.pinion.packets.gui

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.nbt.NbtFactory
import moe.quill.pinion.core.extensions.log
import moe.quill.pinion.core.functional.Lambda
import moe.quill.pinion.core.items.itemBuilder
import moe.quill.pinion.core.menu.MenuItem
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.lang.reflect.InvocationTargetException

private const val ACTION_INDEX = 9
private const val SIGN_LINES = 4
private const val NBT_FORMAT = "{\"text\":\"%s\"}"
private const val NBT_BLOCK_ID = "minecraft:sign"

class SignMenu(
    private val plugin: Plugin,
    val response: (Player, List<String>) -> Unit
) {

    lateinit var position: BlockPosition

    init {
        ProtocolLibrary.getProtocolManager()
            .addPacketListener(object : PacketAdapter(this.plugin, PacketType.Play.Client.UPDATE_SIGN) {
                override fun onPacketReceiving(event: PacketEvent?) {
                    val player = event?.player ?: return
                    if (event.player != player) return
                    event.isCancelled = true

                    response.invoke(player, event.packet.stringArrays.read(0).toList())

                    Lambda {
                        if (!player.isOnline) return@Lambda
                        val location = position.toLocation(player.world)
                        player.sendBlockChange(location, location.block.blockData)
                    }.runTaskLater(plugin, 2L)

                    ProtocolLibrary.getProtocolManager().removePacketListener(this)
                }
            })
    }

    fun open(player: Player) {
        if (!player.isOnline) return
        val location = player.location
        this.position = BlockPosition(location.blockX, location.blockY + (255 - location.blockY), location.blockZ)

        log("Sending Block Change")
        player.sendBlockChange(this.position.toLocation(location.world), Material.OAK_SIGN.createBlockData())

        val openSign = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR)
        val signData = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TILE_ENTITY_DATA)

        openSign.blockPositionModifier.write(0, this.position)

        val signNBT = NbtFactory.ofCompound("")

        signNBT.put("x", position.x)
        signNBT.put("y", position.y)
        signNBT.put("z", position.z)
        signNBT.put("id", NBT_BLOCK_ID)

        signData.blockPositionModifier.write(0, this.position)
        signData.nbtModifier.write(0, signNBT)

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, signData)
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign)
        } catch (exception: InvocationTargetException) {
            exception.printStackTrace()
        }
    }

    /**
     * closes the menu. if force is true, the menu will close and will ignore the reopen
     * functionality. false by default.
     *
     * @param player the player
     * @param force decides whether it will reopen if reopen is enabled
     */
    fun close(player: Player) {
        if (!player.isOnline) return
        player.closeInventory()
    }

    private fun color(input: String): String {
        return ChatColor.translateAlternateColorCodes('&', input)
    }
}

fun textInput(
    plugin: Plugin,
    icon: ItemStack = itemBuilder(Material.NAME_TAG) { name { Component.text("Input Text") } },
    action: (Player, List<String>) -> Unit
): MenuItem = MenuItem(icon) { player -> SignMenu(plugin, action).open(player) }