package moe.quill.pinion.scoreboards

import moe.quill.pinion.core.entries.Entry
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard

import java.util.*

/**
 * The scoreboard class is a class
 * that wraps the standard bukkit scoreboard
 * and uses the concept of 'entries'. Entries
 * can freely be added or removed from the board.
 * this MUST be registered in the 'ScoreboardManager' class
 * for it to be updated and shown to players correctly.
 */
class Scoreboard(title: Entry, slot: DisplaySlot = DisplaySlot.SIDEBAR) {

    private val tools = ScoreboardTools()
    private val serializer = LegacyComponentSerializer.legacyAmpersand()
    private val maxLines = 15

    val bukkitBoard: Scoreboard = tools.getNewBoard()
    var entries = mutableListOf<Entry>()

    private var objective: Objective

    private val viewers = mutableSetOf<UUID>()

    //Scoreboard entries and updating
    private val contents = mutableMapOf<Int, Component>()

    init {
        this.objective = bukkitBoard.registerNewObjective("SB", "dummy", title.get())

        objective.displaySlot = slot
        render()
    }

    fun render() {
        contents.clear()

        var freshContents = getFreshContents()

        //Ensure we have the proper amount of lines
        if (freshContents.size >= maxLines) {
            freshContents = freshContents.subList(0, maxLines)
        }

        for (idx in freshContents.indices) {
            val fresh = freshContents[idx]

            //If there is an old component there then we want to check if they're equal and return out otherwise
            if (idx < contents.size) {
                val stale = contents[idx]
                if (stale != null && serializer.serialize(stale) == serializer.serialize(fresh)) continue
            }

            val entryPrefix = ChatColor.values()[idx]
            val team = tools.getTeam(bukkitBoard, idx.toString())
            val entryName = "$entryPrefix${ChatColor.WHITE}"

            if (!team.hasEntry(entryName)) {
                team.entries.forEach(team::removeEntry)
                team.addEntry(entryName)
            }

            team.prefix(fresh)
            val mapIndex = maxLines - idx
            contents[mapIndex] = fresh
            objective.getScore(entryName).score = mapIndex

        }
    }

    private fun getFreshContents(): List<Component> {
        return entries.map { it.get() }
    }

    fun getViewingPlayers(): List<Player> {
        return viewers.mapNotNull { Bukkit.getPlayer(it) }
    }

    fun addViewer(uuid: UUID) {
        Bukkit.getPlayer(uuid)?.scoreboard = bukkitBoard
        viewers += uuid
    }

    fun removeViewer(uuid: UUID) {
        Bukkit.getPlayer(uuid)?.scoreboard = tools.getNewBoard()
        viewers -= uuid
    }

    fun isViewer(uuid: UUID): Boolean {
        return viewers.contains(uuid)
    }
}
