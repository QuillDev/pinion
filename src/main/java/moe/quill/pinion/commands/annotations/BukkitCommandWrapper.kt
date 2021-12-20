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
import kotlin.reflect.KClassifier

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

    private val innateArgs = listOf<KClassifier>(CommandSender::class)

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
        if (paramArgs.size < command.params.filterNot { isInnate(it.type.classifier) }.size) {
            //TODO: Actually supply what they needed?
            sender.sendMessage(
                Component.text("You must supply all of the required arguments!").color(NamedTextColor.RED)
            )
            return true
        }

        val mappedArgs = mutableMapOf(command.instanceParam to command.groupInstance)
        var argsIndex = 0
        command.params.forEach { param ->
            //Get the class for this param
            val klass = param.type.classifier as? KClass<*> ?: run {
                Bukkit.getLogger().severe("Issue parsing $param")
                return true
            }

            //Process innate args without mutating arguments state
            if (isInnate(klass)) {
                when (klass) {
                    CommandSender::class -> {
                        mappedArgs[param] = sender
                    }
                }
                return@forEach
            }

            val raw = paramArgs[argsIndex]
            argsIndex++
            //Handle argument mapping
            when {
                //Handle Enum Parsing
                klass.java.isEnum -> {
                    val enum = getEnumValues(klass).firstOrNull { it.name.uppercase() == raw } ?: run {
                        sender.sendMessage(
                            Component.text("Invalid value '$raw' for argument ${param.name} !")
                                .append(
                                    Component.text(" Expected type ${klass.simpleName}!")
                                        .color(NamedTextColor.RED)
                                )
                        )
                        return true
                    }
                    mappedArgs[param] = enum
                }
                //Handle player parsing
                klass == Player::class -> {
                    val player = Bukkit.getPlayer(raw) ?: run {
                        sender.sendMessage(
                            Component.text("Could not find a player with the name $raw!").color(NamedTextColor.RED)
                        )
                        return true
                    }
                    mappedArgs[param] = player
                }
                //Parse strings
                klass == String::class -> {
                    mappedArgs[param] = raw
                }
                //Check registered command processors
                commandProcessor.translators.contains(klass) -> {
                    val translation = commandProcessor.translators[klass]!!.translateArgument(raw) ?: run {
                        sender.sendMessage(Component.text("Input value $raw is invalid.").color(NamedTextColor.RED))
                        return true
                    }
                    Bukkit.getLogger().info("Translation result ${translation::class.simpleName}")
                    Bukkit.getLogger().info("Required: ${(param.type.classifier as? KClass<*>)?.simpleName}")
                    mappedArgs[param] = translation
                }
                else -> {}
            }
        }
        Bukkit.getLogger().info("${mappedArgs.size} - ${command.executor.parameters.size}")
        mappedArgs.forEach {
            Bukkit.getLogger()
                .info("Expected: ${(it.key.type.classifier as? KClass<*>)?.simpleName} - Recieved: ${it.value::class.simpleName}")
        }
        command.executor.callBy(mappedArgs)

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

                val param = command.params
                    .filterNot { isInnate(it.type.classifier) }
                    .getOrNull(paramArgs.lastIndex)
                    ?: return mutableListOf()

                val klass = param.type.classifier as? KClass<*> ?: return mutableListOf()

                return processTab(klass, currentArg)
            }
        }
    }

    fun register(plugin: Plugin) = Bukkit.getServer().commandMap.register(plugin.name, this)

    fun processTab(klass: KClass<*>, arg: String?): MutableList<String> {
        return when {
            klass.java.isEnum -> filterInput(
                getEnumValues(klass).map { it.name }.toMutableList(),
                arg
            )
            klass == Player::class ->
                filterInput(Bukkit.getOnlinePlayers().map { it.name }
                    .toMutableList(), arg)
            commandProcessor.translators.contains(klass) -> {
                return filterInput(
                    commandProcessor.translators[klass]!!.translationNames().toMutableList(), arg
                )
            }
            else -> mutableListOf()
        }
    }

    /**
     * Prints out help for this class's children
     */
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

    //Whether this function is in the innate list
    private fun isInnate(classifier: KClassifier?): Boolean {
        classifier ?: return false
        return innateArgs.contains(classifier)
    }

    //TODO: Some general kotlin helper maybe?
    //Get enum values for the given KClass
    private fun getEnumValues(klass: KClass<*>): List<Enum<*>> =
        if (klass.java.isEnum) (klass.java.enumConstants as? Array<Enum<*>>)?.toList() ?: listOf()
        else listOf()

    //Filter tab completion input
    private fun filterInput(list: MutableCollection<String>?, arg: String?): MutableList<String> {
        list ?: return mutableListOf()
        arg ?: return mutableListOf()
        return list.filter { it.lowercase().contains(arg.lowercase()) }.toMutableList()
    }
}