package moe.quill.pinion.core.util

import moe.quill.pinion.core.items.itemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.CreatureSpawnEvent
import kotlin.reflect.KClass

val entityTypeCache = EntityType.values().mapNotNull {
    if (it.entityClass == null) null
    else it.entityClass to it
}.toMap()

/**
 * Spawn an entity
 * NOTE: If you somehow, SOMEHOW manage to choose an entity class without an entity type
 * this will throw an NPE
 */
fun <T : Entity> spawnEntity(klass: KClass<out T>, location: Location, preSpawn: T.() -> Unit = {}): T {
    val type = entityTypeCache[klass.java]!!
    return location.world.spawnEntity(location, type, CreatureSpawnEvent.SpawnReason.CUSTOM) {
        preSpawn(it as T)
    } as T
}

private val airBlockData = Bukkit.createBlockData(Material.AIR)
fun spawnAreaCloud(location: Location): AreaEffectCloud {
    return spawnEntity(AreaEffectCloud::class, location) {
        radius = 0f
        setParticle(Particle.BLOCK_CRACK, airBlockData)
        ticksLived = Int.MAX_VALUE
    }!!
}

fun Entity.removeEverything() = removeRecursively(this)

private fun removeRecursively(entity: Entity) {
    entity.passengers.forEach { removeRecursively(it) }
    entity.remove()
}
