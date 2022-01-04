package moe.quill.pinion.core.items

import moe.quill.pinion.core.architecture.builders.ListMutator
import net.kyori.adventure.text.Component

class Lore(private val item: ItemBuilder) : ListMutator<Component> {
    override val values: MutableList<Component> = item.build().lore() ?: mutableListOf()

    override fun update() {
        item.applyMeta { it.lore(values) }
    }
}