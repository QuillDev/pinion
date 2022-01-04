package moe.quill.pinion.core.architecture.entries

import net.kyori.adventure.text.Component

class MutableEntry(var component: Component) : Entry {
    override fun get(): Component {
        return component
    }
}