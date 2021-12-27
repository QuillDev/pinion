package moe.quill.pinion.toast

import io.papermc.paper.advancement.AdvancementDisplay
import org.bukkit.NamespacedKey
import org.bukkit.advancement.Advancement

class ToasterAdvancement(
    private val _key: NamespacedKey,
    private val _criteria: MutableList<String>,
    private val _display: AdvancementDisplay?,
    private val _parent: Advancement?,
    private val _children: MutableCollection<Advancement>,
    _root: Advancement?
) : Advancement {

    private val rootAdv: Advancement = _root ?: this

    override fun getKey(): NamespacedKey {
        return _key
    }

    override fun getCriteria(): MutableCollection<String> {
        return _criteria
    }

    override fun getDisplay(): AdvancementDisplay? {
        return _display
    }

    override fun getParent(): Advancement? {
        return _parent
    }

    override fun getChildren(): MutableCollection<Advancement> {
        return _children
    }

    override fun getRoot(): Advancement {
        return rootAdv
    }

    fun getHandle(): ToasterAdvancement {
        return this
    }
}