package one.wabbit.data

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * StableName analogue (JVM-only).
 *
 * - Two StableNames are equal iff they were produced (while live) for the *same* object instance.
 * - StableNames don't keep the referent alive (weak keys).
 * - Equality/hash are stable for the lifetime of the token; not stable across processes.
 *
 * NOTE: Interned strings and boxed caches still reflect *identity*. If two vars point to the same
 * JVM instance, their StableNames are equal — by design. You're asking for identity, not morality.
 */
class StableName<out A> internal constructor(internal val token: Token) {

    override fun equals(other: Any?): Boolean =
        other is StableName<*> && this.token === other.token

    override fun hashCode(): Int = token.id.hashCode()

    override fun toString(): String = "StableName(${token.id})"

    internal class Token internal constructor(internal val id: Long)

    companion object {
        private val queue = ReferenceQueue<Any>()
        private val ids = AtomicLong(1)
        // Weak identity keys -> canonical token
        private val map = ConcurrentHashMap<WeakKey, StableName.Token>()

        /**
         * Produce (or fetch) a StableName token for 'value'.
         * Null is a special case: id 0, all nulls share the same StableName.
         */
        @Suppress("UNCHECKED_CAST")
        fun <A> of(value: A): StableName<A> {
            if (value == null) return NULL as StableName<A>
            expungeStale()
            val key = WeakKey(value, queue)
            val tok = map.computeIfAbsent(key) { StableName.Token(ids.getAndIncrement()) }
            return StableName(tok)
        }

        /** Opportunistically clean dead entries. Call is O(dead-entries). */
        fun sweep() = expungeStale()

        @Suppress("UNCHECKED_CAST")
        private val NULL = StableName<Nothing?>(StableName.Token(0))

        private fun expungeStale() {
            var ref = queue.poll()
            while (ref != null) {
                map.remove(ref)   // 'ref' is literally the WeakKey instance used as map key
                ref = queue.poll()
            }
        }

        /**
         * Weak key with identity semantics:
         *   hash = identityHashCode(referent)
         *   equals = referentA === referentB (only when both alive)
         */
        private class WeakKey(
            referent: Any,
            queue: ReferenceQueue<Any>
        ) : WeakReference<Any>(referent, queue) {
            private val hash = System.identityHashCode(referent)
            override fun hashCode(): Int = hash
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is WeakKey) return false
                if (hash != other.hash) return false
                val a = this.get() ?: return false
                val b = other.get() ?: return false
                return a === b
            }
        }
    }
}
