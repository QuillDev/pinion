package moe.quill.pinion.commands.translation

interface CommandArgTranslator<T> {
    fun translationNames(): Collection<String>
    fun translateArgument(arg: String): T?
}