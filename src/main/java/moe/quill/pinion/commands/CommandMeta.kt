package moe.quill.pinion.commands

import moe.quill.pinion.commands.annotations.Command
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

//TODO: Check if there is performance issues on tab completion
data class CommandMeta(
    val command: Command,
    val groupInstance: Any,
    val executor: KFunction<*>,
    val params: List<KClass<*>>
)