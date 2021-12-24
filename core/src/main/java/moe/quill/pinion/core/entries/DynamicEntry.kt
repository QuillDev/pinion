package moe.quill.pinion.core.entries

import net.kyori.adventure.text.Component

class DynamicEntry(private val supplier: () -> Component) : Entry {
    override fun get(): Component {
        return supplier()
    }
}