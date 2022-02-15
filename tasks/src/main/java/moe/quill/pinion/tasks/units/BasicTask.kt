package moe.quill.pinion.tasks.units

import moe.quill.sotv.engine.tasks.units.Task

open class BasicTask(
    identifier: String,
    private val startExecutor: (Task) -> Unit = {},
    private val runExecutor: ((Task) -> Unit)? = null,
    private val endExecutor: (Task) -> Unit = {}
) : Task(identifier) {
    override fun start() {
        startExecutor(this)
    }

    override fun run() {
        runExecutor
            ?.let { it(this) }
            ?: run { complete(); return }
    }

    override fun end() {
        endExecutor(this)
    }
}