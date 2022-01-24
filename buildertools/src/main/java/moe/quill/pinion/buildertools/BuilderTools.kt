package moe.quill.pinion.buildertools;

import moe.quill.pinion.buildertools.commands.BuildToolCommands
import moe.quill.pinion.commands.CommandProcessor
import org.bukkit.plugin.java.JavaPlugin;

class BuilderTools : JavaPlugin() {

    override fun onEnable() {
        //Create the command processor
        val processor = CommandProcessor(this)
        processor.registerCommand(BuildToolCommands(this))
    }
}
