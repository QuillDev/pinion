package moe.quill.pinion.holograms

import moe.quill.pinion.core.architecture.entries.Entry
import moe.quill.pinion.core.functional.Lambda
import org.bukkit.Location
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.plugin.Plugin

class DynamicHologram(
    plugin: Plugin,
    override val location: Location,
    updateRate: Long = 5
) : Hologram {
    override val lines = mutableListOf<Entry>()
    override val entities = mutableListOf<AreaEffectCloud>()

    init {
        Lambda { update() }.runTaskTimer(plugin, 0, updateRate)
    }
}