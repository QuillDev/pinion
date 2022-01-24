package moe.quill.pinion.spawners.lib

import moe.quill.pinion.core.extensions.onlinePlayers
import moe.quill.pinion.core.extensions.registerEvents
import moe.quill.pinion.core.functional.Lambda
import moe.quill.pinion.core.items.itemBuilder
import moe.quill.pinion.core.util.spawnEntity
import moe.quill.pinion.spawners.Spawners
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.CreatureSpawner
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.*

@SerializableAs("Spawner")
class Spawner(
    private val plugin: Plugin,
    var name: String,
    var block: Block,
    visible: Boolean = true,
    enabled: Boolean = true,
    val entityMeta: MutableList<EntityMeta> = mutableListOf(),
    var spawnCap: Int = 3,
    rate: Long = 250,
    radius: Int = 4,
    displayEntity: EntityType = EntityType.CREEPER
) : Listener, ConfigurationSerializable {

    private val random = Random()

    //Visible Setter
    var visible = visible
        set(value) {
            field = value
            updateBlock()
        }
    var enabled = enabled
        set(value) {
            field = value
            startSpawn()
        }

    var radius = radius
        set(value) {
            field = value
            updateCache()
            updateSpawnLocations()
        }
    var rate = rate
        set(value) {
            field = value
            startSpawn()
        }
    var displayEntity = displayEntity
        set(value) {
            field = value
            updateBlock()
        }

    //Current spawned entity information
    val entities = mutableSetOf<Entity>()

    //Spawn Location Information
    val cache = mutableSetOf<Block>()
    private val spawnLocations = mutableSetOf<Block>()

    //Spawning Task
    private var spawnTask: BukkitTask? = null

    init {
        updateBlock()
        updateCache()
        updateSpawnLocations()
        startSpawn()
        plugin.registerEvents(this)
    }

    private fun startSpawn() {
        spawnTask?.cancel()
        if (!enabled) return
        this.spawnTask = Lambda {
            entities.removeIf { !it.isValid }
            spawn()
        }.runTaskTimer(plugin, 0, rate)
    }

    fun spawn() {
        if (entityMeta.isEmpty()) return
        if (entities.size >= spawnCap) return
        if (!block.chunk.isLoaded) return
        if (onlinePlayers().none { it.gameMode != GameMode.SPECTATOR && it.location.distance(block.location) < 16 }) return
        if (block.lightLevel > 7) return
        val block = spawnLocations.randomOrNull() ?: return

        val totalWeight = entityMeta.sumOf { it.weight }
        val weightRoll = random.nextInt(totalWeight)
        var weightSum = 0

        var meta: EntityMeta? = null
        for (curMeta in entityMeta) {
            if (curMeta.weight < (weightRoll - weightSum)) {
                weightSum += curMeta.weight
                continue
            }
            meta = curMeta
            break
        }

        val klass = meta?.type?.entityClass?.kotlin ?: return

        entities += spawnEntity(klass, block.location.add(0.0, 1.0, 0.0)) {
            //Normal Living Entity Properties
            if (this is LivingEntity) {
                isGlowing = meta.glowing
                isInvisible = meta.invisible

                //If the override toggle is on
                if (meta.overrideEquipment) {
                    //Set their equipment
                    equipment?.apply {
                        helmet = meta.helmet
                        chestplate = meta.chest
                        leggings = meta.leggings
                        boots = meta.boots
                        setItemInMainHand(meta.mainHand)
                        setItemInOffHand(meta.offHand)
                    }
                }
            }

            if (this is Ageable) {
                if (meta.isBaby) setBaby()
                else setAdult()
            }

            //Creeper Properties
            if (this is Creeper) run { isPowered = meta.charged }
            //Check size conditions
            if (this is Slime) run { size = meta.size }
            if (this is Phantom) run { size = meta.size }
        }!!
    }

    fun updateBlock() {
        block.type = Material.AIR
        if (!visible) {
            return
        }
        block.type = Material.SPAWNER
        val state = block.state as CreatureSpawner
        state.spawnedType = displayEntity
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
        spawnTask?.cancel()
    }

    //Handle //TODO: Merge keys
    private val spawnerKey = NamespacedKey(plugin, "spawner")

    //Turn this spawner into an item
    fun itemize(): ItemStack {
        val yaml = YamlConfiguration()
        yaml.set("root", this)
        return itemBuilder(Material.SPAWNER) { addKey(spawnerKey, PersistentDataType.STRING, yaml.saveToString()) }
    }

    @EventHandler
    fun onBreakBlock(event: BlockBreakEvent) {
        if (!visible) return
        if (event.block != block) return
        spawnTask?.cancel()
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (!visible) return
        if (!event.blockList().contains(block)) return
        spawnTask?.cancel()
    }

    @EventHandler
    fun onExplosion(event: BlockExplodeEvent) {
        if (!visible) return
        if (!event.blockList().contains(block)) return
        spawnTask?.cancel()
    }

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): Spawner {
            return Spawner(
                JavaPlugin.getPlugin(Spawners::class.java),
                map["name"] as String,
                (map["location"] as Location).block,
                map["visible"] as? Boolean ?: true,
                map["enabled"] as? Boolean ?: true,
                (map["types"] as MutableList<EntityMeta>),
                (map["spawnCap"] as? Number)?.toInt() ?: 7,
                (map["rate"] as Number).toLong(),
                (map["radius"] as Number).toInt(),
                enumValueOf(map["displayEntity"] as? String ?: "CREEPER")
            )
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "name" to name,
            "location" to block.location,
            "visible" to visible,
            "enabled" to enabled,
            "types" to entityMeta,
            "spawnCap" to spawnCap,
            "rate" to rate,
            "radius" to radius,
            "displayEntity" to displayEntity.name
        )
    }
}

