package moe.quill.pinion.core.util

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.CreatureSpawnEvent
import kotlin.reflect.KClass

class EntityUtil {

    companion object {
        @JvmStatic
        fun <T : Entity> spawnEntity(klass: KClass<out T>, location: Location, preSpawn: (T) -> Unit = {}): T? {
            val type = EntityType.values().firstOrNull { it.entityClass == klass.java } ?: return null

            return location.world.spawnEntity(location, type, CreatureSpawnEvent.SpawnReason.CUSTOM) {
                preSpawn(it as T)
            } as T
        }
    }
}