package moe.quill.pinion.spawners;

import com.comphenix.protocol.ProtocolLibrary
import moe.quill.pinion.commands.CommandProcessor
import moe.quill.pinion.core.architecture.Module
import moe.quill.pinion.core.extensions.log
import moe.quill.pinion.glow.GlowHandler
import moe.quill.pinion.spawners.commands.SpawnerCommand
import moe.quill.pinion.spawners.config.SpawnerManager
import moe.quill.pinion.spawners.lib.EntityMeta
import moe.quill.pinion.spawners.lib.Spawner
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class Spawners : JavaPlugin(), Module {
    override val plugin: Plugin = this

    override fun onEnable() {

        //Serialization Registration
        ConfigurationSerialization.registerClass(EntityMeta::class.java)
        ConfigurationSerialization.registerClass(Spawner::class.java)

        //Load Protocl lib
        val protocolManager = ProtocolLibrary.getProtocolManager() ?: run {
            log("Could not get the protocol manager.", Level.WARNING)
            return
        }

        val commandProcessor = CommandProcessor(this)
        val spawnerManager = SpawnerManager(this)
        val glowHandler = GlowHandler()

        commandProcessor.apply {
            registerTranslator(Spawner::class, spawnerManager)
            registerCommand(SpawnerCommand(plugin, glowHandler, spawnerManager))
        }
    }
}
