package moe.quill.pinion.buildertools;

import moe.quill.pinion.commands.CommandProcessor
import org.bukkit.plugin.java.JavaPlugin;

class BuilderTools : JavaPlugin() {

    override fun onEnable() {
        val processor = CommandProcessor(this)
    }
}
