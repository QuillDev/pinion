package moe.quill.pinion.core.extensions

import org.bukkit.Bukkit
import java.util.logging.Level

fun log(message: String, level: Level = Level.INFO) = Bukkit.getLogger().log(level, message)