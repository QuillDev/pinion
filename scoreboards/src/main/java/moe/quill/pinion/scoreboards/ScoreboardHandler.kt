package moe.quill.pinion.scoreboards

import moe.quill.pinion.core.functional.Lambda
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import java.util.*

class ScoreboardHandler(plugin: Plugin, private val updateTicks: Long = 1) : Listener {
    val boards = mutableListOf<Scoreboard>()
    val boardMap = mutableMapOf<UUID, Scoreboard>()

    init {
        Lambda { boards.forEach { it.render() } }.runTaskTimerAsynchronously(plugin, 0, updateTicks)
    }

    fun setBoard(uuid: UUID, scoreboard: Scoreboard) {
        Bukkit.getPlayer(uuid)?.scoreboard = scoreboard.bukkitBoard
        boardMap[uuid] = scoreboard
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onJoin(event: PlayerJoinEvent) {
        boardMap[event.player.uniqueId]?.let { setBoard(event.player.uniqueId, it) }
    }
}