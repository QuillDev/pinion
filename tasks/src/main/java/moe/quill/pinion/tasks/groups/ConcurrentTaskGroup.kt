package moe.quill.pinion.tasks.groups

import moe.quill.sotv.engine.tasks.units.Task

open class ConcurrentTaskGroup(
    identifier: String,
    startExec: () -> Unit = {},
    endExec: () -> Unit = {},
    vararg tasks: Task,
) : TaskGroup(identifier, startExec, endExec, *tasks) {

    override fun run() {
        super.run()
        val runnableTasks = tasks.filter { !it.complete }
        if (runnableTasks.isEmpty()) {
            complete()
            return
        }
        runnableTasks.forEach { it.run() }
    }
}