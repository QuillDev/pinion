package moe.quill.pinion.core.functional;

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class TimedLambda(private val lambda: (BukkitRunnable) -> Unit) {

    private var task: BukkitTask? = null

    fun runTaskLater(plugin: Plugin, delay: Long): BukkitTask {
        val startTime = System.currentTimeMillis() + delay

        return Lambda {
            if (System.currentTimeMillis() < startTime) return@Lambda
            //Run the task when the time is valid
            object : BukkitRunnable() {
                override fun run() {
                    lambda(this)
                }
            }.runTask(plugin)
            //Cancel this
            it.cancel()
        }.runTaskTimer(plugin, 0, 1)
    }

    fun runTaskTimer(plugin: Plugin, delay: Long, interval: Long): BukkitTask {
        return TimedLambda { parent ->
            var nextActivation = 0L
            Lambda child@{ child ->
                if (parent.isCancelled) {
                    child.cancel()
                    return@child
                }

                //Check if we've passed the next activation time
                if (System.currentTimeMillis() < nextActivation) return@child
                object : BukkitRunnable() {
                    override fun run() {
                        lambda(this)
                    }
                }.runTask(plugin)
                nextActivation = System.currentTimeMillis() + interval
            }.runTaskTimer(plugin, 0, 1)

        }.runTaskLater(plugin, delay)
    }
}
