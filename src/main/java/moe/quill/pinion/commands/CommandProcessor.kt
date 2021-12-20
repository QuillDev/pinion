package moe.quill.pinion.commands

import moe.quill.pinion.commands.annotations.BukkitCommandWrapper
import moe.quill.pinion.commands.annotations.Command
import moe.quill.pinion.commands.annotations.CommandGroup
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class CommandProcessor(private val plugin: Plugin) {
    private val commandMap = mutableMapOf<String, MutableMap<String, KFunction<*>>>()
    val translators = mutableMapOf<KClass<*>, CommandArgTranslator<*>>()

    fun <T : Any> registerTranslator(target: KClass<out T>, translator: CommandArgTranslator<T>) {
        translators[target] = translator
        Bukkit.getLogger().info("Registered translator for type ${target::class.simpleName}")
    }

    /**
     * Register a class annotated with the
     */
    fun registerCommand(commandInstance: Any) {
        //Process annotation groups
        val groupAnnotation = commandInstance::class.findAnnotation<CommandGroup>() ?: run {
            Bukkit.getLogger().warning("Attempted to register a non-command ${commandInstance::class.simpleName}!")
            return
        }
        //Get members and log that we're registering them
        val members = commandInstance::class::declaredFunctions.get().filter { it.hasAnnotation<Command>() }
        groupAnnotation.aliases.forEach {
            commandMap.computeIfAbsent(it) { mutableMapOf() }
            Bukkit.getLogger().info("Registered command group '$it' with ${members.size} commands!")
        }

        val commandMeta = mutableListOf<CommandMeta>()
        //Register members by parsing their command arguments
        members.forEach { kFunction ->
            val commandAnnotation = kFunction.findAnnotation<Command>() ?: return@forEach
            val instanceParam = kFunction.parameters.firstOrNull() ?: return@forEach
            //Get all parameters behind the instance call parameter
            commandMeta += CommandMeta(
                commandAnnotation,
                commandInstance,
                instanceParam,
                kFunction,
                kFunction.parameters.drop(1)
            )
        }

        BukkitCommandWrapper(groupAnnotation, commandMeta, this).register(plugin)
    }
}