package moe.quill.pinion.commands

interface CommandArgTranslator<T> {
    fun translationNames(): Collection<String>
    fun translateArgument(string: String): T?
}