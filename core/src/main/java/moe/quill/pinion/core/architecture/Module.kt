package moe.quill.pinion.core.architecture

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import kotlin.reflect.KClass

interface Module : ForwardingAudience {
    val plugin: Plugin

    fun <T : Any> registerService(klass: KClass<T>, provider: T, priority: ServicePriority = ServicePriority.Normal) {
        plugin.server.servicesManager.register(klass.java, provider, plugin, priority)
        plugin.logger.info("Registered service for class '${klass.simpleName}'")
    }

    fun <T : Any> getService(klass: KClass<T>): T? {
        return plugin.server.servicesManager.load(klass.java)
    }

    //Managing Listeners
    fun registerListener(vararg listeners: Listener) {
        listeners.forEach {
            plugin.server.pluginManager.registerEvents(it, plugin)
            Bukkit.getLogger().info("Registered listener '${it::class.simpleName}'!")
        }
    }

    fun unregisterListener(vararg listeners: Listener) {
        listeners.forEach {
            HandlerList.unregisterAll(it)
            Bukkit.getLogger().info("Unregistered listener '${it::class.simpleName}'!")

        }
    }

    override fun audiences(): MutableIterable<Audience> {
        return Bukkit.getServer().onlinePlayers
    }
}