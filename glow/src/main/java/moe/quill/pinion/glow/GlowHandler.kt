package moe.quill.pinion.glow

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import moe.quill.pinion.core.extensions.biCache
import moe.quill.pinion.packets.extensions.createPacket
import moe.quill.pinion.packets.extensions.sendPacket
import moe.quill.pinion.packets.wrappers.EntityMetadataPacket
import moe.quill.pinion.packets.wrappers.SpawnLivingEntityPacket
import moe.quill.pinion.packets.wrappers.serializers.WrappedSerializer
import org.bukkit.block.Block
import org.bukkit.entity.Player

private val glowCache = biCache<Block, Int>()
private val packetCache = mutableMapOf<Int, PacketContainer>()

class GlowHandler {

    fun showGlow(player: Player, block: Block) {
        val entityId = sendSpawnPacket(player, block)
        sendMetadataPacket(player, entityId)
    }

    fun hideGlow(player: Player, block: Block) {
        createHidePacket(block)?.let { player.sendPacket(it) }
    }

    fun sendSpawnPacket(player: Player, block: Block): Int {
        //Try to send a pre cached packet if possible
        val cachedId = glowCache[block]
        if (cachedId != null) {
            packetCache[cachedId]?.let {
                player.sendPacket(it)
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

    fun sendMetadataPacket(player: Player, id: Int) {
        val metaPacket = EntityMetadataPacket(id, 0x40 + 0x20).apply {
            writeObject(16, WrappedSerializer.INT.serializer, 2)
        }
        player.sendPacket(metaPacket.packet)
    }

    fun createHidePacket(block: Block): PacketContainer? {
        val id = glowCache[block] ?: return null
        val packet = PacketType.Play.Server.ENTITY_DESTROY.createPacket()
        packet.intLists.write(0, listOf(id))
        return packet
    }
}