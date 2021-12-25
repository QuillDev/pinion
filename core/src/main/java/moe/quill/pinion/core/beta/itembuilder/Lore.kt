package moe.quill.pinion.core.beta.itembuilder

import moe.quill.pinion.core.builders.ListMutator
import net.kyori.adventure.text.Component

class Lore(private val item: ItemBuilder) : ListMutator<Component> {
    override val list: MutableList<Component> = item.build().lore() ?: mutableListOf()

    override fun update() {
        item.applyMeta { it.lore(list) }
    }
}