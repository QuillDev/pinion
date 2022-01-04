package moe.quill.pinion.packets.wrappers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import moe.quill.pinion.packets.PacketWrapper
import moe.quill.pinion.packets.toPacket
import org.bukkit.Location
import java.util.*

/**
 * Entity type can be found here
 * see <a href="https://wiki.vg/Entity_metadata#Entity_Metadata_Format"/>
 */
class SpawnLivingEntityPacket(
    val entityType: Int,
    val location: Location,
    val entityId: Int = (Math.random() * Int.MAX_VALUE).toInt(),
    val uuid: UUID = UUID.randomUUID()
) : PacketWrapper {
    override val packet: PacketContainer = PacketType.Play.Server.SPAWN_ENTITY_LIVING.toPacket()

    init {
        packet.integers
            .write(0, entityId) //The Entity ID
            .write(1, entityType) //Type of entity

        packet.uuiDs.write(0, uuid) //Entity UUID

        //Position
        packet.doubles
            .write(0, location.x) //X
            .write(1, location.y) //Y
            .write(2, location.z) //Z
    }
}