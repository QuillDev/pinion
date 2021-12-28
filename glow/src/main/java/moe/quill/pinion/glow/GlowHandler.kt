package moe.quill.pinion.glow

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import moe.quill.pinion.core.functional.Lambda
import org.apache.commons.lang.math.RandomUtils.nextInt
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin
import java.util.*


private val byteSerializer = WrappedDataWatcher.Registry.get(java.lang.Byte::class.java)
private val intSerializer = WrappedDataWatcher.Registry.get(java.lang.Integer::class.java)

private const val spawnFlags = (0x40 + 0x20).toByte()

class GlowHandler(plugin: Plugin) : Listener, PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {

    private val idToBlock = mutableMapOf<Int, Block>()
    private val blockToId = mutableMapOf<Block, Int>()

    private val glowCache = mutableMapOf<UUID, MutableSet<Block>>()
    private val targets = mutableMapOf<UUID, MutableSet<Block>>()

    init {
        ProtocolLibrary.getProtocolManager().addPacketListener(this)
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        //Respawn all of the the targets
        targets[event.player.uniqueId]
            ?.forEach { showPlayer(event.player, it) }
    }

    @EventHandler
    fun onQuit(event: PlayerDeathEvent) {
        //Remove the data from the id cache
        glowCache[event.player.uniqueId]
            ?.forEach { blockToId[it]?.let { data -> idToBlock -= data } }
    }

    override fun onPacketReceiving(event: PacketEvent?) {
        val packetType = event?.packetType ?: return
        if (packetType != PacketType.Play.Client.USE_ENTITY) return

        val packet = event.packet

        if (packet.enumEntityUseActions.read(0).action.name != "INTERACT_AT") return
        val targetId = packet.integers.read(0)
        val block = idToBlock[targetId] ?: return

        Lambda {
            plugin.server.pluginManager.callEvent(
                PlayerInteractEvent(
                    event.player,
                    Action.RIGHT_CLICK_BLOCK,
                    null,
                    block,
                    BlockFace.SELF,
                    EquipmentSlot.HAND
                )
            )
        }.runTask(plugin)
    }

    fun hidePlayer(player: Player, block: Block) {
        val entityId = blockToId[block] ?: return
        val killPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)
        killPacket.intLists.write(0, listOf(entityId))
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, killPacket)

        idToBlock -= entityId
        blockToId -= block
        targets[player.uniqueId]?.remove(block)
    }

    fun showPlayer(player: Player, block: Block) {
        val entityUUID = UUID.randomUUID()

        block.type = Material.CHEST

        val entityID = nextInt()
        val spawnPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING)
        spawnPacket.integers.write(0, entityID)
            .write(1, 80)

        //UUID for the entity
        spawnPacket.uuiDs.write(0, entityUUID)
        //Location of the entity
        spawnPacket.doubles
            .write(0, block.x + .5)
            .write(1, block.y.toDouble())
            .write(2, block.z + .5)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawnPacket)

        val metaPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA)
        val meta = WrappedDataWatcher(player)
        meta.setObject(0, byteSerializer, spawnFlags)
        meta.setObject(16, intSerializer, 2)
        metaPacket.integers.write(0, entityID)
        metaPacket.watchableCollectionModifier.write(0, meta.watchableObjects)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, metaPacket)

        //Register it to the glow data for interactions
        idToBlock[entityID] = block
        blockToId[block] = entityID
        //Register it to the targets for respawning between disconnects
        targets.computeIfAbsent(player.uniqueId) { mutableSetOf() } += block
    }
}

