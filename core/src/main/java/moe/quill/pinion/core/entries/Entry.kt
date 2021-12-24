package moe.quill.pinion.core.entries

import net.kyori.adventure.text.Component

interface Entry {
    fun get(): Component
}