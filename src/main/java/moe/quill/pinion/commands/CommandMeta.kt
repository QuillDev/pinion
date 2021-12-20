package moe.quill.pinion.commands

import moe.quill.pinion.commands.annotations.Command
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

//TODO: Check if there is performance issues on tab completion
data class CommandMeta(
    val command: Command,
    val groupInstance: Any,
    val instanceParam: KParameter,
    val executor: KFunction<*>,
    val params: List<KParameter>
)