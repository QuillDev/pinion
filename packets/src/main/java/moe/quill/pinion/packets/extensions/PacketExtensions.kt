package moe.quill.pinion.packets.extensions

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer

//Packet Type Extensions
fun PacketType.createPacket(): PacketContainer = ProtocolLibrary.getProtocolManager().createPacket(this)