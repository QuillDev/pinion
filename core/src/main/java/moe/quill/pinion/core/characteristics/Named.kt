package moe.quill.pinion.core.characteristics

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration

fun Collection<Named>.join(delimiter: Component, nameModifier: (Component) -> Component = { it }): Component =
    Component.join(
        JoinConfiguration.separator(delimiter),
        this.map { nameModifier(Component.text(it.name)) }
    )

interface Named {
    val name: String
}