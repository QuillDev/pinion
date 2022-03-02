package moe.quill.pinion.core.extensions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import java.time.Duration
import kotlin.reflect.jvm.internal.impl.descriptors.Named

private val rainbowColors = listOf(
    NamedTextColor.RED,
    NamedTextColor.GOLD,
    NamedTextColor.YELLOW,
    NamedTextColor.GREEN,
    NamedTextColor.DARK_PURPLE,
    NamedTextColor.LIGHT_PURPLE,
    NamedTextColor.WHITE
)

class ComponentBuilder(base: TextComponent = Component.empty()) {
    var builder = base.toBuilder()

    fun append(child: () -> Component) {
        builder = builder.append(child())
    }

    fun color(color: () -> TextColor) {
        builder = builder.color(color())
    }

    fun bold(bold: () -> Boolean) {
        builder = builder.decoration(TextDecoration.BOLD, bold())
    }

    fun italic(italic: () -> Boolean) {
        builder = builder.decoration(TextDecoration.ITALIC, italic())
    }

    fun strikethrough(strikethrough: () -> Boolean) {
        builder = builder.decoration(TextDecoration.STRIKETHROUGH, strikethrough())
    }

    fun style(style: () -> Style) {
        builder = builder.style(style())
    }

    fun clickEvent(clickEvent: () -> ClickEvent) {
        builder = builder.clickEvent(clickEvent())
    }

    fun <V> hoverEvent(hoverEvent: () -> HoverEvent<V>) {
        builder = builder.hoverEvent(hoverEvent())
    }

    fun rainbow() {
        val stale = builder.build()
        var rainbowIndex = 0

        val rainbowBuilder = text {
            stale.content().toCharArray().forEach { char ->
                if (char.isWhitespace()) {
                    +text(char.toString())
                    return@forEach
                }
                +text(char.toString()) { style { stale.style() }; color { rainbowColors[rainbowIndex] } }

                rainbowIndex++
                if (rainbowIndex >= rainbowColors.size) rainbowIndex = 0
            }
        }
        this.builder = (rainbowBuilder as TextComponent).toBuilder()
    }

    operator fun Component.unaryPlus() {
        builder = builder.append(this)
    }

    operator fun TextDecoration.unaryPlus() {
        builder = builder.decoration(this, true)
    }

    operator fun TextDecoration.unaryMinus() {
        builder = builder.decoration(this, false)
    }

    operator fun TextColor.unaryPlus() {
        color { this }
    }

    operator fun ClickEvent.unaryPlus() {
        clickEvent { this }
    }

    operator fun <V> HoverEvent<V>.unaryPlus() {
        hoverEvent { this }
    }

    fun build(): Component {
        return builder.build()
    }
}

fun text(content: Any = "", actions: ComponentBuilder.() -> Unit = {}): Component {
    val builder = ComponentBuilder(Component.text(content.toString()))
    builder.actions()
    return builder.build()
}

fun color(r: Int, g: Int, b: Int): TextColor {
    return TextColor.color(r, g, b)
}

fun times(
    fadeIn: Long = Title.DEFAULT_TIMES.fadeIn().toMillis(),
    stay: Long = Title.DEFAULT_TIMES.stay().toMillis(),
    fadeOut: Long = Title.DEFAULT_TIMES.fadeOut().toMillis()
): Title.Times {
    return Title.Times.of(
        Duration.ofMillis(fadeIn),
        Duration.ofMillis(stay),
        Duration.ofMillis(fadeOut)
    )
}

fun title(
    title: Component = Component.empty(),
    subtitle: Component = Component.empty(),
    times: Title.Times = Title.DEFAULT_TIMES
): Title {
    return Title.title(title, subtitle, times)
}

val newline = Component.newline()
val space = Component.space()

//Create the color aliases
val DARK_BLUE = NamedTextColor.DARK_BLUE
val BLACK = NamedTextColor.BLACK
val DARK_GREEN = NamedTextColor.DARK_GREEN
val DARK_RED = NamedTextColor.DARK_RED
val DARK_AQUA = NamedTextColor.DARK_AQUA
val DARK_PURPLE = NamedTextColor.DARK_PURPLE
val GOLD = NamedTextColor.GOLD
val GRAY = NamedTextColor.GRAY
val DARK_GRAY = NamedTextColor.DARK_GRAY
val BLUE = NamedTextColor.BLUE
val GREEN = NamedTextColor.GREEN
val AQUA = NamedTextColor.AQUA
val RED = NamedTextColor.RED
val LIGHT_PURPLE = NamedTextColor.LIGHT_PURPLE
val YELLOW = NamedTextColor.YELLOW
val WHITE = NamedTextColor.WHITE

//Decorations
val BOLD = TextDecoration.BOLD
val STRIKETHROUGH = TextDecoration.STRIKETHROUGH
val ITALIC = TextDecoration.ITALIC
val OBFUSCATED = TextDecoration.OBFUSCATED
val UNDERLINED = TextDecoration.UNDERLINED
