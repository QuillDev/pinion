package moe.quill.pinion.tasks.groups

import moe.quill.sotv.engine.tasks.units.Task

open class LinearTaskGroup(
    identifier: String,
    startExec: () -> Unit = {},
    endExec: () -> Unit = {},
    vararg tasks: Task,
) : TaskGroup(identifier, startExec, endExec, *tasks) {

    private var taskIdx = 0

    override fun run() {
        super.run()
        //Check if this module should be completed
        if (tasks.isEmpty() || taskIdx >= tasks.size) {
            complete()
            return
        }
        val activeTask = tasks[taskIdx]
        activeTask.run()
        if (activeTask.complete) taskIdx++
    }
}