package moe.quill.pinion.packets.wrappers.serializers

import com.comphenix.protocol.wrappers.WrappedDataWatcher

enum class WrappedSerializer(val serializer: WrappedDataWatcher.Serializer) {
    BYTE(WrappedDataWatcher.Registry.get(java.lang.Byte::class.java)),
    INT(WrappedDataWatcher.Registry.get(java.lang.Integer::class.java))
}