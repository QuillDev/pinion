package moe.quill.pinion.core.entities.ai

import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class MovementCircularBehavior(
    center: Location,
    private val radius: Double,
    resolution: Int,
    private val shift: Double
) : MovementBehavior(center, 360 * resolution) {

    override fun update(): Location {
        val progress = ticks / resolution.toFloat() * 360f + shift
        val x = cos(progress) * radius
        val z = sin(progress) * radius
        if (ticks >= resolution) {
            ticks = 0
        } else {
            ticks += 1
        }
        return center.clone().add(x, 0.0, z)
            .setDirection(
                Vector(
                    cos((progress + 90)) * radius,
                    0.0,
                    sin((progress + 90)) * radius
                ).normalize()
            )
    }
}