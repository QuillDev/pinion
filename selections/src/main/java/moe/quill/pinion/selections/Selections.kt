package moe.quill.pinion.selections;

import moe.quill.pinion.commands.CommandProcessor
import moe.quill.pinion.core.architecture.Module
import moe.quill.pinion.selectapi.components.*
import moe.quill.pinion.selectapi.components.handler.SelectionHandler
import moe.quill.pinion.selections.commands.BasicCommands
import moe.quill.pinion.selections.handler.SelectionHandlerImpl
import moe.quill.pinion.selections.listeners.SelectionListener
import org.bukkit.NamespacedKey
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

class Selections : JavaPlugin(), Module {
    override val plugin = this

    override fun onEnable() {

        //Register Serializations
        ConfigurationSerialization.registerClass(LocationGroup::class.java)
        ConfigurationSerialization.registerClass(NamedLocation::class.java)
        ConfigurationSerialization.registerClass(Bounds::class.java)
        ConfigurationSerialization.registerClass(Zone::class.java)
        ConfigurationSerialization.registerClass(Schematic::class.java)

        //commands
        val commandProcessor = CommandProcessor(this)
        //Register the selection service
        val selectionHandler = SelectionHandlerImpl(this, commandProcessor)
        registerService(SelectionHandler::class, selectionHandler)

        //The selection tool key
        val toolKey = NamespacedKey(this, "selection_tool")
        commandProcessor.registerCommand(BasicCommands(toolKey))
        registerListener(SelectionListener(toolKey, selectionHandler))
    }
}
