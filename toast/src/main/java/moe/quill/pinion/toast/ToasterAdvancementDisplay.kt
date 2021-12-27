package moe.quill.pinion.toast

import io.papermc.paper.advancement.AdvancementDisplay
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

class ToasterAdvancementDisplay(
    private val _frame: AdvancementDisplay.Frame,
    private val _title: Component,
    private val _description: Component,
    private val _icon: ItemStack,
    private val _doesShowToast: Boolean,
    private val _doesAnnounceToChat: Boolean,
    private val _isHidden: Boolean,
    private val _backgroundPath: NamespacedKey?
) : AdvancementDisplay {
    override fun frame(): AdvancementDisplay.Frame {
        return _frame
    }

    override fun title(): Component {
        return _title
    }

    override fun description(): Component {
        return _description
    }

    override fun icon(): ItemStack {
        return _icon
    }

    override fun doesShowToast(): Boolean {
        return _doesShowToast
    }

    override fun doesAnnounceToChat(): Boolean {
        return _doesAnnounceToChat
    }

    override fun isHidden(): Boolean {
        return _isHidden
    }

    override fun backgroundPath(): NamespacedKey? {
        return _backgroundPath
    }
}