package moe.quill.pinion.core.extensions

/**
 * Create a Bi-directional cache backed by hashmaps,
 * this should **ONLY** be used when **BOTH** keys and values
 * will be unique in the map.
 */
class BiCache<T, Z>(vararg initial: Pair<T, Z>) {

    private val leftCache = mutableMapOf<T, Z>()
    private val rightCache = mutableMapOf<Z, T>()

    init {
        initial.forEach { (left, right) ->
            leftCache[left] = right
            rightCache[right] = left
        }
    }

    fun leftValues(): Collection<Z> {
        return leftCache.values
    }

    fun rightValues(): Collection<T> {
        return rightCache.values
    }

    @JvmName("setLeft")
    operator fun set(key: T, value: Z) {
        leftCache[key] = value
        rightCache[value] = key
    }

    @JvmName("setRight")
    operator fun set(key: Z, value: T) {
        set(value, key)
    }

    @JvmName("getLeft")
    operator fun get(key: T): Z? {
        return leftCache[key]
    }

    @JvmName("getRight")
    operator fun get(key: Z): T? {
        return rightCache[key]
    }

    @JvmName("minusAssignLeft")
    operator fun minusAssign(key: T) {
        val value = leftCache[key] ?: return
        leftCache -= key
        rightCache -= value
    }

    @JvmName("minusAssignRight")
    operator fun minusAssign(key: Z) {
        val value = rightCache[key] ?: return
        rightCache -= key
        leftCache -= value
    }
}

fun <T, Z> biCache(vararg initial: Pair<T, Z>) = BiCache(*initial)


