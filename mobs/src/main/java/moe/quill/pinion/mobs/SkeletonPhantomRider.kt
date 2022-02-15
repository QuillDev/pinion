package moe.quill.pinion.mobs

import moe.quill.pinion.core.util.spawnEntity
import moe.quill.pinion.mobs.vanilla.aggressive
import moe.quill.pinion.mobs.vanilla.forcePlayerTargets
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Phantom
import org.bukkit.entity.Skeleton
import org.bukkit.plugin.Plugin

class SkeletonPhantomRider(plugin: Plugin, override val location: Location) : QuillEntity {
    override val entities = run {
        val entities = mutableListOf<Entity>()
        //Spawn the Phantom
        spawnEntity(Phantom::class, location) {
            //Spawn the Skeleton
            addPassenger(spawnEntity(Skeleton::class, location) {
                forcePlayerTargets(plugin)
                entities += this
            }!!)
            //Set the phantom to be aggro
            aggressive(plugin)
            entities += this
        }!!
        entities
    }
}