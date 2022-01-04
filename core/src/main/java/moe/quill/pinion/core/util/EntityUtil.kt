package moe.quill.pinion.core.util

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

fun <T : Entity> spawnEntity(klass: KClass<out T>, location: Location, preSpawn: (T) -> Unit = {}): T? {
    val type = entityTypeCache[klass.java] ?: return null
    return location.world.spawnEntity(location, type, CreatureSpawnEvent.SpawnReason.CUSTOM) {
        preSpawn(it as T)
    } as T
}

private val airBlockData = Bukkit.createBlockData(Material.AIR)
fun spawnAreaCloud(location: Location): AreaEffectCloud {
    return spawnEntity(AreaEffectCloud::class, location) {
        it.radius = 0f
        it.setParticle(Particle.BLOCK_CRACK, airBlockData)
        it.ticksLived = Int.MAX_VALUE
    }!!
}

fun Entity.removeEverything() = removeRecursively(this)

private fun removeRecursively(entity: Entity) {
    entity.passengers.forEach { removeRecursively(it) }
    entity.remove()
}
