package moe.quill.pinion.core.geometry

import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

class BoxUtil {
    companion object {
        @JvmStatic
        fun getPoints(box: BoundingBox): List<Vector> {
            return listOf(
                box.min,
                Vector(box.minX, box.minY, box.maxZ),
                Vector(box.minX, box.maxY, box.minZ),
                Vector(box.minX, box.maxY, box.maxZ),
                box.max,
                Vector(box.maxX, box.maxY, box.minZ),
                Vector(box.maxX, box.minY, box.maxZ),
                Vector(box.maxX, box.minY, box.minZ)
            )
        }
    }

}