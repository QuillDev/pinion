package moe.quill.pinion.spawners.lib

import moe.quill.pinion.core.characteristics.Named
import moe.quill.pinion.core.functional.Lambda
import moe.quill.pinion.core.util.spawnEntity
import moe.quill.pinion.spawners.Spawners
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.CreatureSpawner
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

@SerializableAs("Spawner")
class Spawner(
    plugin: Plugin,
    override val name: String,
    var block: Block,
    visible: Boolean = true,
    val entityMeta: MutableList<EntityMeta> = mutableListOf(),
    var rate: Long = 100,
    var radius: Int = 3
) : Listener, Named, ConfigurationSerializable {

    //Visible Setter
    var visible = visible
        set(value) {
            field = value
            block.type = if (visible) Material.SPAWNER else Material.AIR
        }

    private val spawnCap = 7
    private val entities = mutableSetOf<Entity>()

    private val cache = mutableSetOf<Block>()
    private val spawnLocations = mutableSetOf<Block>()
    private val spawnTask: BukkitTask

    init {
        block.type = if (visible) Material.SPAWNER else Material.AIR
        if (visible) {
            entities.firstOrNull()?.let {
                val state = block.state as CreatureSpawner
                state.spawnedType = it.type
            }
        }

        updateCache()
        updateSpawnLocations()
        Bukkit.getServer().pluginManager.registerEvents(this, plugin)
        this.spawnTask = Lambda {
            entities.removeIf { !it.isValid }
            spawn()
        }.runTaskTimer(plugin, 0, rate)
    }

    fun spawn() {
        if (entities.size >= spawnCap) return
        if (!block.chunk.isLoaded) return
        if (Bukkit.getOnlinePlayers().none { it.location.distance(block.location) < 25 }) return
        if (block.lightLevel > 7) return
        val block = spawnLocations.randomOrNull() ?: return
        val meta = entityMeta.randomOrNull() ?: return

        val klass = meta.type.entityClass?.kotlin ?: return

        entities += spawnEntity(klass, block.location.add(0.0, 1.0, 0.0)) { entity ->
            //Normal Living Entity Properties
            if (entity is LivingEntity) {
                entity.isGlowing = meta.glowing
                entity.isInvisible = meta.invisible

                //Set their equipment
                entity.equipment?.helmet = meta.helmet
                entity.equipment?.chestplate = meta.chest
                entity.equipment?.leggings = meta.leggings
                entity.equipment?.boots = meta.boots
                entity.equipment?.setItemInMainHand(meta.mainHand)
                entity.equipment?.setItemInOffHand(meta.offHand)
            }

            //Creeper Properties
            if (entity is Creeper) run { entity.isPowered = meta.charged }

            //Check size conditions
            if (entity is Slime) run { entity.size = meta.size }
            if (entity is Phantom) run { entity.size = meta.size }
        }!!
    }

    private fun updateSpawnLocations() {
        spawnLocations.clear()
        spawnLocations += cache.filter {
            val above = it.getRelative(BlockFace.UP)
            val twoAbove = above.getRelative(BlockFace.UP)
            it.type != Material.AIR && above.type == Material.AIR && twoAbove.type == Material.AIR
        }
    }

    private fun updateCache() {
        cache.clear()
        val origin = block.location
        val originX = origin.x
        val originY = origin.y
        val originZ = origin.z

        val offset = origin.clone()
        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in -radius..radius) {
                    offset.x = originX + x
                    offset.y = originY + y
                    offset.z = originZ + z
                    val block = offset.block
                    cache += block
                }
            }
        }
    }

    fun disable() {
        block.type = Material.AIR
        spawnTask.cancel()
    }

    @EventHandler
    fun onBreakBlock(event: BlockBreakEvent) {
        if (!visible) return
        if (event.block != block) return
        spawnTask.cancel()
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (!visible) return
        if (!event.blockList().contains(block)) return
        spawnTask.cancel()
    }

    @EventHandler
    fun onExplosion(event: BlockExplodeEvent) {
        if (!visible) return
        if (!event.blockList().contains(block)) return
        spawnTask.cancel()
    }


    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): Spawner {
            return Spawner(
                JavaPlugin.getPlugin(Spawners::class.java),
                map["name"] as String,
                (map["location"] as Location).block,
                map["visible"] as? Boolean ?: true,
                (map["types"] as MutableList<EntityMeta>),
                (map["rate"] as Number).toLong(),
                (map["radius"] as Number).toInt()
            )
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "name" to name,
            "location" to block.location,
            "visible" to visible,
            "types" to entityMeta,
            "rate" to rate,
            "radius" to radius
        )
    }
}

