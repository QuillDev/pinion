package moe.quill.pinion.core.ui

import moe.quill.pinion.core.extensions.registerEvents
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin


fun BossBar.persist(plugin: Plugin): PersistentBossBar = PersistentBossBar(plugin, this)

class PersistentBossBar(private val plugin: Plugin, val bar: BossBar) : Listener {

    init {
        plugin.registerEvents(this)
        Bukkit.getServer().showBossBar(bar)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.showBossBar(bar)
    }

    fun stop() {
        HandlerList.unregisterAll(this)
        Bukkit.getServer().hideBossBar(bar)
    }
}