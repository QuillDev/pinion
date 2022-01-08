package moe.quill.pinion.packets.wrappers

import com.comphenix.protocol.events.PacketContainer

interface PacketWrapper {
    val packet: PacketContainer
}