package moe.quill.pinion.core.extensions

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

fun log(message: String, level: Level = Level.INFO) = Bukkit.getLogger().log(level, message)

//Plugin Extensions
fun Plugin.registerEvents(vararg events: Listener) = events.forEach { server.pluginManager.registerEvents(it, this) }
fun unregisterEvents(vararg events: Listener) = events.forEach { HandlerList.unregisterAll(it) }

fun onlinePlayers(): List<Player> = Bukkit.getOnlinePlayers().toList()