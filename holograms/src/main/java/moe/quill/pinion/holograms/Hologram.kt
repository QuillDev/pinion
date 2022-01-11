package moe.quill.pinion.holograms

import moe.quill.pinion.core.architecture.entries.Entry
import moe.quill.pinion.core.util.spawnEntity
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

    val bottomUp: Boolean

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
            entities += spawnEntity(
                AreaEffectCloud::class,
                location.clone().add(
                    0.0,
                    (entities.size * (lineSize * (if (bottomUp) 1 else -1))).toDouble(),
                    0.0
                )
            ) {
                isCustomNameVisible = true
                setParticle(Particle.BLOCK_CRACK, Material.AIR.createBlockData())
                ticksLived = Int.MAX_VALUE
                radius = 0f
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