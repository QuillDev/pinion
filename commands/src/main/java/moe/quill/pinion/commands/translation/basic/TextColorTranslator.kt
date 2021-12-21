package moe.quill.pinion.commands.translation.basic

import moe.quill.pinion.commands.translation.CommandArgTranslator
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

class TextColorTranslator : CommandArgTranslator<TextColor> {
    override fun translationNames(): Collection<String> {
        return listOf("r,g,b", "#000000")
    }

    override fun translateArgument(arg: String): TextColor? {
        run {
            val split = arg.split(',')
            if (split.size == 3) {
                val r = split[0].toIntOrNull() ?: return@run
                val g = split[1].toIntOrNull() ?: return@run
                val b = split[2].toIntOrNull() ?: return@run
                if (r < 256 || b < 256 || g < 256) {
                    return TextColor.color(r, g, b)
                }
            }
        }

        run {
            if (!arg.startsWith('#')) return@run
            val hex = arg.removePrefix("#").toIntOrNull(16) ?: return@run
            return TextColor.color(hex)
        }

        return NamedTextColor.WHITE
    }
}