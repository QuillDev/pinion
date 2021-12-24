package moe.quill.pinion.scoreboards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

class ScoreboardTools {

    fun getNewBoard(): Scoreboard {
        return Bukkit.getScoreboardManager().newScoreboard
    }

    fun getObjective(scoreboard: Scoreboard, name: String = "dummy", entry: Component = Component.empty()): Objective {
        return scoreboard.getObjective(name) ?: scoreboard.registerNewObjective(name, "dummy", entry)
    }

    fun getTeam(scoreboard: Scoreboard, name: String): Team {
        val teamName = if (name.isEmpty()) name else name.substring(0, name.length.coerceAtMost(15))
        return scoreboard.getTeam(teamName) ?: scoreboard.registerNewTeam(teamName)
    }

    fun setGlowColor(scoreboard: Scoreboard, entity: Entity, color: NamedTextColor) {
        val team = getTeam(scoreboard, color.examinableName())
        team.color(color)
        entity.isGlowing = true

        if (entity is Player) {
            team.addEntry(entity.name)
            return
        }

        team.addEntry(entity.uniqueId.toString())
    }
}