package moe.quill.pinion.core.util

import moe.quill.pinion.core.extensions.biCache
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.CreatureSpawnEvent
import kotlin.reflect.KClass

val entityTypeCache = biCache(*EntityType.values().mapNotNull {
    val klass = it.entityClass ?: return@mapNotNull null
    return@mapNotNull it to klass
}.toTypedArray())

fun <T : Entity> spawnEntity(klass: KClass<out T>, location: Location, preSpawn: (T) -> Unit = {}): T? {
    val type = entityTypeCache[klass.java] ?: return null
    return location.world.spawnEntity(location, type, CreatureSpawnEvent.SpawnReason.CUSTOM) {
        preSpawn(it as T)
    } as T
}