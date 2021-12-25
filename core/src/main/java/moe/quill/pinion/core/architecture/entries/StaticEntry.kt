package moe.quill.pinion.core.architecture.entries

import net.kyori.adventure.text.Component

class StaticEntry(private val component: Component) : Entry {
    override fun get(): Component {
        return component
    }
}