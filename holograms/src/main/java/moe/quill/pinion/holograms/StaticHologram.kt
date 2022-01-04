package moe.quill.pinion.holograms

import moe.quill.pinion.core.architecture.entries.Entry
import org.bukkit.Location
import org.bukkit.entity.AreaEffectCloud

class StaticHologram(
    override val location: Location,
    override val bottomUp: Boolean = false
) : Hologram {
    override val lines = mutableListOf<Entry>()
    override val entities = mutableListOf<AreaEffectCloud>()
}