package moe.quill.pinion.packets.wrappers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import moe.quill.pinion.packets.PacketWrapper
import moe.quill.pinion.packets.toPacket

class EntityMetadataPacket(
    entityId: Int,
    meta: Byte = 0x00,
    init: EntityMetadataPacket.() -> Unit = {}
) :
    PacketWrapper {

    override val packet: PacketContainer = PacketType.Play.Server.ENTITY_METADATA.toPacket()
    private val watcher = WrappedDataWatcher()

    init {
        packet.integers.write(0, entityId)
        writeObject(0, WrappedSerializer.BYTE, meta)
        init()
    }

    fun writeObject(idx: Int, typeSerializer: WrappedSerializer, value: Any) {
        watcher.setObject(idx, typeSerializer.serializer, value)
        packet.watchableCollectionModifier.write(0, watcher.watchableObjects)
    }
}