package moe.quill.pinion.glow

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import io.papermc.paper.text.PaperComponents
import moe.quill.pinion.core.extensions.biCache
import moe.quill.pinion.packets.wrappers.SpawnLivingEntityPacket
import moe.quill.pinion.packets.sendPacket
import moe.quill.pinion.packets.wrappers.EntityMetadataPacket
import moe.quill.pinion.packets.wrappers.WrappedSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.block.Block
import org.bukkit.entity.Player

val glowCache = biCache<Block, Int>()
val packetCache = mutableMapOf<Int, PacketContainer>()

private val byteSerializer = WrappedDataWatcher.Registry.get(java.lang.Byte::class.java)
private val intSerializer = WrappedDataWatcher.Registry.get(java.lang.Integer::class.java)

fun showGlow(player: Player, block: Block) {
    val entityId = sendSpawnPacket(player, block)
    sendMetadataPacket(player, entityId)
}

fun hideGlow(player: Player, block: Block) {
    createHidePacket(block)?.let { sendPacket(player, it) }
}

fun sendSpawnPacket(player: Player, block: Block): Int {
    //Try to send a pre cached packet if possible
    val cachedId = glowCache[block]
    if (cachedId != null) {
        packetCache[cachedId]?.let {
            sendPacket(player, it)
            return cachedId
        }
    }

    val wrapper = SpawnLivingEntityPacket(80, block.location.add(.5, 0.0, .5))
    val packet = wrapper.packet
    player.sendPacket(packet)
    val entityId = wrapper.entityId
    glowCache[block] = entityId
    packetCache[entityId] = packet
    return entityId
}

private val serializer = PaperComponents.gsonSerializer()
fun sendMetadataPacket(player: Player, id: Int) {
    val metaPacket = EntityMetadataPacket(id, 0x40 + 0x20) {
        writeObject(16, WrappedSerializer.INT, 2)
//        writeObject(
//            2, WrappedDataWatcher.Registry.getChatComponentSerializer(), WrappedChatComponent.fromJson(
//                serializer.serialize(Component.text("This is a test").color(NamedTextColor.GREEN))
//            )
//        )

    }
    player.sendPacket(metaPacket.packet)
}

fun sendPacket(player: Player, container: PacketContainer) {
    ProtocolLibrary.getProtocolManager().sendServerPacket(player, container)
}

fun createPacket(type: PacketType): PacketContainer {
    return ProtocolLibrary.getProtocolManager().createPacket(type)
}

fun createHidePacket(block: Block): PacketContainer? {
    val id = glowCache[block] ?: return null
    val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)
    packet.intLists.write(0, listOf(id))
    return packet
}