package moe.quill.pinion.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.Player

fun Player.sendPacket(packet: PacketContainer) =
        ProtocolLibrary.getProtocolManager().sendServerPacket(this, packet)

fun PacketType.toPacket() = ProtocolLibrary.getProtocolManager().createPacket(this)

fun String.shout() = run { this.uppercase() }
