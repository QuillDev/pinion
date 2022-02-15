package moe.quill.sotv.engine.tasks.units

import moe.quill.pinion.core.extensions.log
import moe.quill.pinion.core.items.itemBuilder
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import kotlin.properties.Delegates

abstract class Task(val identifier: String) : ForwardingAudience {

    var started = false
    var complete = false

    var startTime by Delegates.notNull<Long>()

    open fun run() {
        if (!started) {
            start()
            //Logging Actions
            log("Started task $identifier!")
        }
    }

    fun complete() {
        if (complete) return
        end()
        complete = true
        //Logging Actions
        log("Completed task $identifier | Total Time Elapsed: ${System.currentTimeMillis() - startTime}")
    }

    open fun start() {
        this.startTime = System.currentTimeMillis()
        this.started = true

        itemBuilder(Material.STICK) {
            name { Component.text("Stick of Truth") }
            modelData { 2 }
        }
    }

    abstract fun end()

    override fun audiences(): MutableIterable<Audience> {
        return Bukkit.getOnlinePlayers()
    }
}