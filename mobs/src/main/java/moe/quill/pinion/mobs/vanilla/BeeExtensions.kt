package moe.quill.pinion.mobs.vanilla

import moe.quill.pinion.core.extensions.onlinePlayers
import moe.quill.pinion.core.functional.Lambda
import org.bukkit.entity.Bee
import org.bukkit.plugin.Plugin

fun Bee.setNeverHive(plugin: Plugin) = run {
    Lambda {
        if (!isValid) {
            it.cancel()
            return@Lambda
        }
        cannotEnterHiveTicks = Int.MAX_VALUE
    }.runTaskTimer(plugin, 0, 20)
}

fun Bee.setAggressive(plugin: Plugin) = run {
    Lambda {
        if (!isValid) {
            it.cancel()
            return@Lambda
        }

        for (player in onlinePlayers()) {
            if (player.location.distanceSquared(location) < 16) {
                target = player
                break
            }
        }
        this.anger = Int.MAX_VALUE
    }.runTaskTimer(plugin, 0, 100)
}