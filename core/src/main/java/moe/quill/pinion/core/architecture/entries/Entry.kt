package moe.quill.pinion.core.architecture.entries

import net.kyori.adventure.text.Component

interface Entry {
    fun get(): Component
}