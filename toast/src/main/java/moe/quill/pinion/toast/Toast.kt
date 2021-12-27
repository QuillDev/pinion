package moe.quill.pinion.toast

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.MinecraftKey
import com.comphenix.protocol.wrappers.WrappedChatComponent
import moe.quill.pinion.toast.packets.WrapperPlayServerAdvancements
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin


fun sendPacket(player: Player, plugin: Plugin) {

    val x = WrapperPlayServerAdvancements()
    x.setReset(false)
    x.setAdvancements(mapOf(MinecraftKey("test", "one") to TSAdvancement()))

    ProtocolLibrary.getProtocolManager().sendServerPacket(player, x.handle)
}

class TSAdvancement() : WrapperPlayServerAdvancements.SerializedAdvancement() {

    init {
        key = MinecraftKey("test", "one")
        advancement = null
        display = TSDisplay()
        rewards = null
        criteria = mapOf()
        requirements = arrayOf()
    }
}

class TSDisplay() : WrapperPlayServerAdvancements.AdvancementDisplay() {

    init {
        title = WrappedChatComponent.fromText("Hello World")
        description = WrappedChatComponent.fromText("This is cool!")
        icon = ItemStack(Material.FEATHER)
        background = null
        frame = WrapperPlayServerAdvancements.FrameType.CHALLENGE
        showToast = true
        announceToChat = true
        hidden = false
        xCoord = 0f
        yCoord = 0f
    }

}