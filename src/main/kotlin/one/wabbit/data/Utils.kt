package one.wabbit.data

import java.util.*
import kotlin.collections.ArrayList

fun <T> identitySet(): MutableSet<T> =
    Collections.newSetFromMap(IdentityHashMap<T, Boolean>())

fun <T> identitySetOf(vararg elements: T): MutableSet<T> =
    identitySet<T>().also { it.addAll(elements) }

fun <K, V> identityMap(): MutableMap<K, V> =
    IdentityHashMap()

fun <T> Iterable<T>.distinctByIdentity(): List<T> {
    val seen = IdentityHashMap<T, Boolean>()
    val out = ArrayList<T>()
    for (e in this) if (seen.put(e, true) == null) out += e
    return out
}

fun <T> Sequence<T>.distinctByIdentity(): Sequence<T> = sequence {
    val seen = IdentityHashMap<T, Boolean>()
    for (e in this@distinctByIdentity) if (seen.put(e, true) == null) yield(e)
}
