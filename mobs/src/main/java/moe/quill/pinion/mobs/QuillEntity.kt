package moe.quill.pinion.mobs

import org.bukkit.Location
import org.bukkit.entity.Entity

interface QuillEntity {

    val location: Location
    val entities: List<Entity>
    fun validate(): Boolean = entities.all { it.isValid }
}