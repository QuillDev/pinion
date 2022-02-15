package moe.quill.pinion.mobs.vanilla

import moe.quill.pinion.core.extensions.onlinePlayers
import moe.quill.pinion.core.functional.Lambda
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Mob
import org.bukkit.plugin.Plugin

fun Mob.forcePlayerTargets(plugin: Plugin) = run {
    modifyBehavior(plugin, {
        if (target?.type == EntityType.PLAYER) return@modifyBehavior
        target = null
    }, interval = 20)
}

fun Mob.aggressive(plugin: Plugin, range: Int = 32, interval: Long = 100) = run {
    modifyBehavior(
        plugin, {
            target = onlinePlayers().filter { player ->
                location.distance(player.location) <= range && (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE)
            }.minByOrNull { player -> player.location.distance(location) }
        }, interval = interval
    )
}

fun Entity.modifyBehavior(plugin: Plugin, action: Entity.() -> Unit = {}, delay: Long = 0, interval: Long = 20) = run {
    Lambda {
        if (!isValid) {
            it.cancel()
            return@Lambda
        }
        action()
    }.runTaskTimer(plugin, delay, interval)
}