package moe.quill.pinion.core.functional;

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.atomic.AtomicLong

class TimedLambda(private val lambda: () -> Unit) {

    private var task: BukkitTask? = null

    fun cancel() {
        task?.cancel()
    }

    fun runTaskTimer(plugin: Plugin, interval: Long): BukkitTask {

        val lastRun = AtomicLong(0)

        val runner = Lambda {
            if (System.currentTimeMillis() - lastRun.get() < interval) return@Lambda;
            lambda()
            lastRun.set(System.currentTimeMillis())
        }.runTaskTimer(plugin, 0, 1);

        this.task = runner
        return runner
    }

    /**
     * Run a task x milliseconds later
     *
     * @param plugin to register this under
     * @param delay to run later
     * @return the bukkit task for this runnable
     */
    fun runTaskLater(plugin: Plugin, delay: Long): BukkitTask {
        val runTime = System.currentTimeMillis() + delay;

        val runner = Lambda {
            if (System.currentTimeMillis() < runTime) return@Lambda
            lambda()
            cancel();
        }.runTaskTimer(plugin, 0, 10);

        this.task = runner
        return runner;
    }
}
