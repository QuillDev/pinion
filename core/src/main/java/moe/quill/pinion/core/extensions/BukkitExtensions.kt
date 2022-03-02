package moe.quill.pinion.core.extensions

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import java.util.logging.Level

fun log(message: String, level: Level = Level.INFO) = Bukkit.getLogger().log(level, message)

//Plugin Extensions
fun Plugin.registerEvents(vararg events: Listener) = events.forEach { server.pluginManager.registerEvents(it, this) }
fun unregisterEvents(vararg events: Listener) = events.forEach { HandlerList.unregisterAll(it) }

fun onlinePlayers(): List<Player> = Bukkit.getOnlinePlayers().toList()

fun Entity.persist() = run {
    isPersistent = true

    if (this is LivingEntity) {
        removeWhenFarAway = false
    }

    if (this is Item) {
        setWillAge(false)
    }
}

fun Collection<UUID>.toPlayers(): MutableList<Player> = mapNotNull { Bukkit.getPlayer(it) }.toMutableList()

fun Material.prettyName(): String = run {
    name.split("_").joinToString(" ") {
        it.lowercase().replaceFirstChar { first -> first.uppercase() }
    }
}

fun Material.fetchBlockData(): BlockData = run {
    return blockDataCache[this] ?: run {
        val newData = createBlockData()
        blockDataCache[this] = newData
        newData
    }
}
