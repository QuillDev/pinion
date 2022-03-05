package moe.quill.pinion.core.extensions

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData

//Drop by giving a material, fallback to this blocks material, fetch it otherwise
fun Block.drop(type: Material? = null) = run {
    drop(type?.fetchBlockData() ?: blockData)
}

fun Block.drop(data: BlockData = this.blockData) = run {
    if (type == Material.AIR) return@run
    type = Material.AIR
    val dropLoc = location.clone().add(0.5, 0.0, 0.5)
    dropLoc.world.spawnFallingBlock(dropLoc, data)
}

fun Collection<Block>.replace(type: Material) {
    replace(type.fetchBlockData())
}

fun Collection<Block>.replace(data: BlockData) {
    forEach { it.blockData = data }
}

fun Block.center(): Location {
    return location.add(0.5, 0.0, 0.5)
}