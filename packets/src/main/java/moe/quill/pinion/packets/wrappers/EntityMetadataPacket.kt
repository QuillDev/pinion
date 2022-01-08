package moe.quill.pinion.packets.wrappers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import moe.quill.pinion.packets.extensions.createPacket
import moe.quill.pinion.packets.wrappers.serializers.WrappedSerializer

class EntityMetadataPacket(
    entityId: Int,
    meta: Byte = 0x00
) : PacketWrapper {
    override val packet: PacketContainer = PacketType.Play.Server.ENTITY_METADATA.createPacket()
    private val watcher = WrappedDataWatcher()

    init {
        packet.integers.write(0, entityId)
        writeObject(0, WrappedSerializer.BYTE.serializer, meta)
    }

    fun writeObject(idx: Int, serializer: WrappedDataWatcher.Serializer, value: Any) {
        watcher.setObject(idx, serializer, value)
        packet.watchableCollectionModifier.write(0, watcher.watchableObjects)
    }
}