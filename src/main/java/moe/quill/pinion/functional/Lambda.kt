package moe.quill.pinion.functional

import org.bukkit.scheduler.BukkitRunnable

open class Lambda(private val runner: (Lambda) -> Unit) : BukkitRunnable() {

    override fun run() {
        runner(this)
    }
}