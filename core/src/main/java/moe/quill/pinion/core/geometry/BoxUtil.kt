package moe.quill.pinion.core.geometry

import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

fun BoundingBox.getPoints(): List<Vector> = run {
    return listOf(
        this.min,
        Vector(this.minX, this.minY, this.maxZ),
        Vector(this.minX, this.maxY, this.minZ),
        Vector(this.minX, this.maxY, this.maxZ),
        this.max,
        Vector(this.maxX, this.maxY, this.minZ),
        Vector(this.maxX, this.minY, this.maxZ),
        Vector(this.maxX, this.minY, this.minZ)
    )
}