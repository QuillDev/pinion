package moe.quill.pinion.commands.translation.basic

import moe.quill.pinion.commands.translation.CommandArgTranslator
import org.bukkit.Color

class ColorTranslator : CommandArgTranslator<Color> {
    override fun translateArgument(arg: String): Color? {
        val split = arg.split(',')

        //Allow them to specify RGB comma seperated
        run {
            if (split.size == 3) {
                val r = split[0].toIntOrNull() ?: return@run
                val g = split[1].toIntOrNull() ?: return@run
                val b = split[2].toIntOrNull() ?: return@run
                if (r < 256 || b < 256 || g < 256) {
                    return Color.fromRGB(r, g, b)
                }
            }
        }

        run {
            if (!arg.startsWith('#')) return@run
            val hex = arg.removePrefix("#").toIntOrNull(16) ?: return@run
            return Color.fromRGB(hex)
        }

        return Colors.values().firstOrNull { arg.uppercase() == it.name.uppercase() }?.color
    }

    override fun translationNames(): Collection<String> {
        val colors = Colors.values().map { it.name }.toMutableList()
        colors += "#0000FF"
        colors += "r,g,b"
        return colors
    }
}

private enum class Colors(val color: Color) {
    WHITE(Color.WHITE),
    SILVER(Color.SILVER),
    GRAY(Color.GRAY),
    BLACK(Color.BLACK),
    RED(Color.RED),
    MAROON(Color.MAROON),
    YELLOW(Color.YELLOW),
    OLIVE(Color.OLIVE),
    LIME(Color.LIME),
    GREEN(Color.GREEN),
    AQUA(Color.AQUA),
    TEAL(Color.TEAL),
    BLUE(Color.BLUE),
    NAVY(Color.NAVY),
    FUCHSIA(Color.FUCHSIA),
    PURPLE(Color.PURPLE),
    ORANGE(Color.ORANGE),
}