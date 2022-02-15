package moe.quill.pinion.tasks.groups

import moe.quill.sotv.engine.tasks.units.Task

open class TaskGroup(
    identifier: String,
    private val startExec: () -> Unit = {},
    private val endExec: () -> Unit = {},
    vararg tasks: Task
) : Task(identifier) {
    val tasks = tasks.toMutableList()

    override fun start() {
        super.start()
        startExec()
    }


    override fun end() {
        endExec()
    }
}