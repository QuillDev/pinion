package moe.quill.pinion.commands.translation.basic

import moe.quill.pinion.commands.translation.CommandArgTranslator
import net.kyori.adventure.text.format.NamedTextColor

class NamedTextColorTranslator : CommandArgTranslator<NamedTextColor> {
    override fun translationNames(): Collection<String> {
        return ColorBind.values().map { it.name }
    }

    override fun translateArgument(arg: String): NamedTextColor? {
        return ColorBind.values().firstOrNull { it.name.uppercase() == arg.uppercase() }?.color
    }
}

private enum class ColorBind(val color: NamedTextColor) {
    BLACK(NamedTextColor.BLACK),
    DARK_BLUE(NamedTextColor.DARK_BLUE),
    DARK_GREEN(NamedTextColor.DARK_GREEN),
    DARK_AQUA(NamedTextColor.DARK_AQUA),
    DARK_RED(NamedTextColor.DARK_RED),
    DARK_PURPLE(NamedTextColor.DARK_PURPLE),
    GOLD(NamedTextColor.GOLD),
    GRAY(NamedTextColor.GRAY),
    DARK_GRAY(NamedTextColor.DARK_GRAY),
    BLUE(NamedTextColor.BLUE),
    GREEN(NamedTextColor.GREEN),
    AQUA(NamedTextColor.AQUA),
    RED(NamedTextColor.RED),
    LIGHT_PURPLE(NamedTextColor.LIGHT_PURPLE),
    YELLOW(NamedTextColor.YELLOW),
    WHITE(NamedTextColor.WHITE)
}
