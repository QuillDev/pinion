package moe.quill.pinion.core.entities.ai

import org.bukkit.Location

abstract class MovementBehavior(val center: Location, val resolution: Int) {
    var ticks: Long = 0

    abstract fun update(): Location?
}