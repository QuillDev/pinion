package moe.quill.pinion.core.builders

interface ListMutator<T> {
    val list: MutableList<T>

    fun update()

    operator fun T.unaryPlus() = run { list += this; update() }
    operator fun T.unaryMinus() = run { list -= this; update() }

    operator fun Array<T>.unaryPlus() = run { list += this; update() }
    operator fun Array<T>.unaryMinus() = run { list -= this.toSet(); update() }

    operator fun Collection<T>.unaryPlus() = run { list += this; update() }
    operator fun Collection<T>.unaryMinus() = run { list -= this.toSet(); update() }
}