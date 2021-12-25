package moe.quill.pinion.holograms

import moe.quill.pinion.core.entries.Entry
import moe.quill.pinion.core.util.EntityUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.AreaEffectCloud

interface Hologram {

    val lineSize: Float get() = 0.27f

    val location: Location
    val lines: MutableList<Entry>
    val entities: MutableList<AreaEffectCloud>

    fun addLine(entry: Entry) {
        lines += entry
        update()
    }

    fun removeLine(entry: Entry) {
        lines -= entry
        update()
    }

    fun removeLine(idx: Int) {
        val line = lines.getOrNull(idx) ?: return
        removeLine(line)
    }

    fun remove() {
        entities.forEach { it.remove() }
    }

    fun update() {
        //Create any entities we need if there are more lines
        while (entities.size < lines.size) {
            entities += EntityUtil.spawnEntity(
                AreaEffectCloud::class,
                location.add(
                    0.0,
                    (entities.size * lineSize).toDouble(),
                    0.0
                )
            ) {
                it.isCustomNameVisible = true
                it.setParticle(Particle.BLOCK_CRACK, Bukkit.createBlockData(Material.AIR))
                it.ticksLived = Int.MAX_VALUE
                it.radius = 0f
            } ?: return
        }
        entities.dropLast(entities.size - lines.size)

        for (idx in entities.indices) {
            val line = lines[idx]
            val fresh = line.get()
            entities[idx].customName(fresh)
        }
    }
}