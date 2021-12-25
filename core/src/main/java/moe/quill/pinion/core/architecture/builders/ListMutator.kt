package moe.quill.pinion.core.architecture.builders

interface ListMutator<T> {
    val values: MutableList<T>

    fun update()

    fun set(vararg fresh: T) {
        this.values.clear()
        this.values += fresh
        update()
    }

    fun set(fresh: List<T>) {
        this.values.clear()
        this.values += fresh
        update()
    }

    fun T.setValue() = run { values.clear(); values += this; update() }
    operator fun T.unaryPlus() = run { values += this; update() }
    operator fun T.unaryMinus() = run { values -= this; update() }

    operator fun Array<T>.unaryPlus() = run { values += this; update() }
    operator fun Array<T>.unaryMinus() = run { values -= this.toSet(); update() }

    operator fun Collection<T>.unaryPlus() = run { values += this; update() }
    operator fun Collection<T>.unaryMinus() = run { values -= this.toSet(); update() }
}