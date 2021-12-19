package moe.quill.pinion.commands.annotations

import moe.quill.pinion.commands.CommandMeta
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class BukkitCommandWrapper(
    group: CommandGroup,
    meta: List<CommandMeta>
) : Command(
    group.name,
    group.description,
    "/${group.name}",
    group.aliases.toList()
) {
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

        if (args.isEmpty()) {
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

                if (idx < bindings.size - 1) {
                    result = result.append(Component.newline())
                }
            }

            sender.sendMessage(result)
            return true
        }

        return true
    }

    override fun tabComplete(
        sender: CommandSender,
        alias: String,
        args: Array<out String>,
        location: Location?
    ): MutableList<String> {
        return super.tabComplete(sender, alias, args, location)
    }

    fun register(plugin: Plugin) {
        Bukkit.getServer().commandMap.register(plugin.name, this)
    }
}