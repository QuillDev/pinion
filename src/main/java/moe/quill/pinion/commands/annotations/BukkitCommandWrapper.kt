package moe.quill.pinion.commands.annotations

import moe.quill.pinion.commands.CommandMeta
import moe.quill.pinion.commands.CommandProcessor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass

class BukkitCommandWrapper(
    group: CommandGroup,
    meta: List<CommandMeta>,
    private val commandProcessor: CommandProcessor
) : Command(
    group.name,
    group.description,
    "/${group.name}",
    group.aliases.toList()
) {

    private val innateArgs = listOf(CommandSender::class)

    //Bindings including all aliases and base name
    private val aliasBindings = mutableMapOf<String, CommandMeta>()

    //Bindings including only the primary name
    private val nameBindings = mutableMapOf<String, CommandMeta>()

    init {
        //Bind the command name and all aliases
        meta.forEach {
            nameBindings[it.command.name] = it
            aliasBindings[it.command.name] = it
            it.command.aliases.forEach { alias -> aliasBindings[alias] = it }
        }
    }

    override fun execute(sender: CommandSender, commandLabel: String, rawArgs: Array<out String>): Boolean {
        val args = rawArgs.toList()

        //If no args are supplied
        if (args.isEmpty()) {
            printHelp(sender)
            return true
        }

        val commandName = args[0]
        val command = aliasBindings[commandName] ?: run {
            sender.sendMessage(
                Component.text("No command exists with the name $commandName!").color(NamedTextColor.RED)
            )
            return true
        }

        val paramArgs = args.drop(1)
        if (paramArgs.size < command.params.filterNot { innateArgs.contains(it) }.size) {
            //TODO: Actually supply what they needed?
            sender.sendMessage(
                Component.text("You must supply all of the required arguments!").color(NamedTextColor.RED)
            )
            return true
        }

        val mappedArgs = mutableListOf<Any>()
        var argsIndex = 0

        command.params.forEach { param ->
            //Process innate args without mutating arguments state
            if (innateArgs.contains(param)) {
                when (param) {
                    CommandSender::class -> {
                        mappedArgs.add(sender)
                    }
                }
                return@forEach
            }

            val raw = paramArgs[argsIndex]
            argsIndex++
            //Handle argument mapping
            when {
                //Handle Enum Parsing
                param.java.isEnum -> {
                    val enum = getEnumValues(param).firstOrNull { it.name.uppercase() == raw } ?: run {
                        sender.sendMessage(
                            Component.text("Invalid value '$raw'!")
                                .append(
                                    Component.text(" Expected type ${param.simpleName}!")
                                        .color(NamedTextColor.RED)
                                )
                        )
                        return true
                    }
                    mappedArgs.add(enum)
                }
                //Handle player parsing
                param == Player::class -> {
                    val player = Bukkit.getPlayer(raw) ?: run {
                        sender.sendMessage(
                            Component.text("Could not find a player with the name $raw!").color(NamedTextColor.RED)
                        )
                        return true
                    }
                    mappedArgs.add(player)
                }
                //TODO Number parsing
                //Parse strings
                param == String::class -> {
                    mappedArgs.add(raw)
                }
                //Check registered command processors
                commandProcessor.translators.contains(param) -> {
                    val translation = commandProcessor.translators[param]!!.translateArgument(raw) ?: run {
                        sender.sendMessage(Component.text("Input value $raw is invalid.").color(NamedTextColor.RED))
                        return true
                    }
                    mappedArgs.add(translation)
                }
                else -> {}
            }
        }

        command.executor.call(command.groupInstance, *mappedArgs.toTypedArray())

        return true
    }

    override fun tabComplete(
        sender: CommandSender,
        alias: String,
        rawArgs: Array<out String>,
        location: Location?
    ): MutableList<String> {
        val args = rawArgs.toList()
        return when (args.size) {
            0, 1 -> filterInput(aliasBindings.keys, args.getOrNull(0) ?: "")
            else -> {
                val command = aliasBindings[args[0]] ?: return mutableListOf()

                //Args for determining parameters
                val paramArgs = args.drop(1)
                val currentArg = paramArgs.last()

                //Get any non-innate arguments
                val activeParam = command.params
                    .filterNot { innateArgs.contains(it) }
                    .getOrNull(paramArgs.lastIndex)
                    ?: return mutableListOf()

                return when {
                    activeParam.java.isEnum -> filterInput(
                        getEnumValues(activeParam).map { it.name }.toMutableList(),
                        currentArg
                    )
                    activeParam == Player::class ->
                        filterInput(Bukkit.getOnlinePlayers().map { it.name }
                            .toMutableList(), currentArg)
                    commandProcessor.translators.contains(activeParam) -> {
                        return filterInput(
                            commandProcessor.translators[activeParam]!!.translationNames().toMutableList(), currentArg
                        )
                    }
                    else -> mutableListOf()
                }
            }
        }

    }

    fun register(plugin: Plugin) = Bukkit.getServer().commandMap.register(plugin.name, this)

    private fun printHelp(sender: CommandSender) {
        var result = Component.text()
            .append(Component.text(name).color(NamedTextColor.GOLD))
            .append(Component.space())
            .append(Component.text("command help").color(NamedTextColor.GRAY))
            .append(Component.newline())

        val bindings = nameBindings.values.toList()
        for (idx in bindings.indices) {
            val meta = bindings[idx]
            result = result.append(Component.text(meta.command.name).color(NamedTextColor.GOLD))
                .append(Component.space())
                .append(Component.text(" - ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(meta.command.description).color(NamedTextColor.GRAY))

            if (idx < bindings.lastIndex) {
                result = result.append(Component.newline())
            }
        }

        sender.sendMessage(result)
    }

    private fun getEnumValues(klass: KClass<*>): List<Enum<*>> =
        if (klass.java.isEnum) (klass.java.enumConstants as? Array<Enum<*>>)?.toList() ?: listOf()
        else listOf()

    private fun filterInput(list: MutableCollection<String>?, arg: String?): MutableList<String> {
        list ?: return mutableListOf()
        arg ?: return mutableListOf()
        return list.filter { it.lowercase().contains(arg.lowercase()) }.toMutableList()
    }
}