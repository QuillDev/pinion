package moe.quill.pinion.core.functional

class ConditionalLambda(runner: (Lambda) -> Unit, private val condition: () -> Boolean) : Lambda(runner) {

    override fun run() {
        super.run()

        //If the condition is met, cancel
        if (condition()) {
            cancel()
        }
    }
}