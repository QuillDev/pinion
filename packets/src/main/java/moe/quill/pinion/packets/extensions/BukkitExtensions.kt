package moe.quill.pinion.packets.extensions

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.Player

//Send packet to player
fun Player.sendPacket(packet: PacketContainer) = ProtocolLibrary.getProtocolManager().sendServerPacket(this, packet)