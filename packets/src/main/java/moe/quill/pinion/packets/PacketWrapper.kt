package moe.quill.pinion.packets

import com.comphenix.protocol.events.PacketContainer

interface PacketWrapper {
    val packet: PacketContainer
}