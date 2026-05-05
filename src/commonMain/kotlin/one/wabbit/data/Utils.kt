// SPDX-License-Identifier: AGPL-3.0-or-later

package one.wabbit.data

import kotlin.collections.AbstractMutableMap
import kotlin.collections.AbstractMutableSet

private class IdentityEntry<K, V>(override val key: K, override var value: V) :
    MutableMap.MutableEntry<K, V> {
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

    override fun put(key: K, value: V): V? = backing.put(Ref(key), IdentityEntry(key, value))?.value

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

    override fun remove(element: T): Boolean = backing.remove(element) != null

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

/**
 * Creates a mutable set whose membership test uses reference identity.
 *
 * Adding two distinct objects that are structurally equal stores both objects. Adding the same
 * object reference twice stores it once. Lookup and removal use the same identity semantics.
 *
 * @param T The element type.
 * @return A new empty mutable identity set.
 */
fun <T> identitySet(): MutableSet<T> = IdentitySetImpl()

/**
 * Creates a mutable identity set containing [elements].
 *
 * Duplicate references are collapsed; distinct references are retained even when they compare equal
 * by `equals`. Lookup and removal use reference identity.
 *
 * @param T The element type.
 * @param elements Initial elements to add.
 * @return A new mutable identity set.
 */
fun <T> identitySetOf(vararg elements: T): MutableSet<T> =
    identitySet<T>().also { it.addAll(elements) }

/**
 * Creates a mutable map whose key lookup uses reference identity.
 *
 * Keys are matched by `===`, not by `equals`. Values keep ordinary value semantics. The returned
 * map still implements Kotlin's mutable map views; entry-set membership and removal match entry
 * keys by identity and entry values by structural equality (`==`).
 *
 * @param K The key type.
 * @param V The value type.
 * @return A new empty mutable identity map.
 */
fun <K, V> identityMap(): MutableMap<K, V> = IdentityMapImpl()

/**
 * Returns the first occurrence of each distinct object reference in this iterable.
 *
 * The result preserves iteration order. Objects that compare equal but are not the same reference
 * are both retained.
 *
 * @param T The element type.
 * @return A list containing only the first occurrence of each reference identity.
 */
fun <T> Iterable<T>.distinctByIdentity(): List<T> {
    val seen = identitySet<T>()
    val out = ArrayList<T>()
    for (e in this) {
        if (seen.add(e)) out += e
    }
    return out
}

/**
 * Lazily filters this sequence to the first occurrence of each distinct object reference.
 *
 * The returned sequence preserves source order and keeps a mutable identity set while it is
 * iterated. The internal set is discarded when that iteration completes; starting a new iteration
 * creates a fresh set.
 *
 * @param T The element type.
 * @return A sequence containing only the first occurrence of each reference identity.
 */
fun <T> Sequence<T>.distinctByIdentity(): Sequence<T> = sequence {
    val seen = identitySet<T>()
    for (e in this@distinctByIdentity) {
        if (seen.add(e)) yield(e)
    }
}
