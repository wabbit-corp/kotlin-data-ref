package one.wabbit.data

import kotlin.collections.AbstractMutableMap
import kotlin.collections.AbstractMutableSet

private class IdentityEntry<K, V>(override val key: K, override var value: V) : MutableMap.MutableEntry<K, V> {
    override fun setValue(newValue: V): V {
        val oldValue = value
        value = newValue
        return oldValue
    }
}

private class IdentityMapImpl<K, V> : AbstractMutableMap<K, V>() {
    private val backing = HashMap<Ref<K>, IdentityEntry<K, V>>()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> =
        object : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
            override val size: Int
                get() = backing.size

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
                val iterator = backing.values.iterator()
                return object : MutableIterator<MutableMap.MutableEntry<K, V>> {
                    override fun hasNext(): Boolean = iterator.hasNext()

                    override fun next(): MutableMap.MutableEntry<K, V> = iterator.next()

                    override fun remove() {
                        iterator.remove()
                    }
                }
            }

            override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
                val hadKey = backing.containsKey(Ref(element.key))
                this@IdentityMapImpl[element.key] = element.value
                return !hadKey
            }

            override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean {
                val existing = backing[Ref(element.key)] ?: return false
                return existing.value == element.value
            }

            override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
                if (!contains(element)) return false
                backing.remove(Ref(element.key))
                return true
            }
        }

    override fun put(key: K, value: V): V? =
        backing.put(Ref(key), IdentityEntry(key, value))?.value

    override fun containsKey(key: K): Boolean = backing.containsKey(Ref(key))

    override fun get(key: K): V? = backing[Ref(key)]?.value

    override fun remove(key: K): V? = backing.remove(Ref(key))?.value

    override fun clear() {
        backing.clear()
    }
}

private class IdentitySetImpl<T> : AbstractMutableSet<T>() {
    private val backing = IdentityMapImpl<T, Unit>()

    override val size: Int
        get() = backing.size

    override fun add(element: T): Boolean = backing.put(element, Unit) == null

    override fun contains(element: T): Boolean = backing.containsKey(element)

    override fun iterator(): MutableIterator<T> {
        val iterator = backing.entries.iterator()
        return object : MutableIterator<T> {
            override fun hasNext(): Boolean = iterator.hasNext()

            override fun next(): T = iterator.next().key

            override fun remove() {
                iterator.remove()
            }
        }
    }

    override fun clear() {
        backing.clear()
    }
}

fun <T> identitySet(): MutableSet<T> = IdentitySetImpl()

fun <T> identitySetOf(vararg elements: T): MutableSet<T> =
    identitySet<T>().also { it.addAll(elements) }

fun <K, V> identityMap(): MutableMap<K, V> = IdentityMapImpl()

fun <T> Iterable<T>.distinctByIdentity(): List<T> {
    val seen = identitySet<T>()
    val out = ArrayList<T>()
    for (e in this) {
        if (seen.add(e)) out += e
    }
    return out
}

fun <T> Sequence<T>.distinctByIdentity(): Sequence<T> = sequence {
    val seen = identitySet<T>()
    for (e in this@distinctByIdentity) {
        if (seen.add(e)) yield(e)
    }
}
