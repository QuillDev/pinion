package moe.quill.pinion.core.extensions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration


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

    operator fun Component.unaryPlus() {
        builder = builder.append(this)
    }

    operator fun TextDecoration.unaryPlus() {
        builder = builder.decoration(this, true)
    }

    operator fun TextDecoration.unaryMinus() {
        builder = builder.decoration(this, false)
    }


    fun build(): Component {
        return builder.build()
    }
}

fun text(content: String = "", actions: ComponentBuilder.() -> Unit): Component {
    val builder = ComponentBuilder(Component.text(content))
    builder.actions()
    return builder.build()
}

fun color(r: Int, g: Int, b: Int): TextColor {
    return TextColor.color(r, g, b)
}