package moe.quill.pinion.commands.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class Command(
    val name: String,
    val description: String = "",
    val permission: String = "",
    val aliases: Array<String> = [],
    val senderType: SenderType = SenderType.BOTH,
)
