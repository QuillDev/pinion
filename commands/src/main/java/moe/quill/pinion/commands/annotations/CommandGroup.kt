package moe.quill.pinion.commands.annotations

@Target(AnnotationTarget.CLASS)
annotation class CommandGroup(
    val name: String,
    val description: String = "",
    val permission: String = "",
    val aliases: Array<String> = []
)
