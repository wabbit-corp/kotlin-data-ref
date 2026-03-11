package one.wabbit.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class Box(val id: Int)

class RefTest {
    @Test
    fun equality_reflexive_symmetric_transitive() {
        val obj = Box(1)
        val r1 = Ref(obj)
        val r2 = Ref(obj)
        val r3 = Ref(obj)

        assertEquals(r1, r1)
        assertEquals(r1, r2)
        assertEquals(r2, r1)
        assertEquals(r1, r3)
        assertEquals(r2, r3)

        val sameByValue = Box(1)
        val rDiff = Ref(sameByValue)
        assertNotEquals(r1, rDiff)
    }

    @Test
    fun equality_with_null_and_hashcode_zero() {
        val r1 = Ref<Any?>(null)
        val r2 = Ref<Any?>(null)

        assertEquals(r1, r2)
        assertEquals(0, r1.hashCode())
        assertEquals(r1.hashCode(), r2.hashCode())
        assertEquals("Ref[null]", r1.toString())
    }

    @Test
    fun hash_contract_and_set_behavior() {
        val obj = Any()
        val a = Ref(obj)
        val b = Ref(obj)
        val c = Ref(Any())

        assertEquals(a.hashCode(), b.hashCode())

        val set = HashSet<Ref<Any>>()
        assertTrue(set.add(a))
        assertTrue(set.contains(b))
        assertFalse(set.contains(c))

        val map = HashMap<Ref<Any>, String>()
        map[a] = "hit"
        assertEquals("hit", map[b])
        assertNull(map[c])
    }

    @Test
    fun variance_does_not_break_equality() {
        val x = charArrayOf('x').concatToString()
        val rString: Ref<String> = Ref(x)
        val rCharSeq: Ref<CharSequence> = Ref(x)
        assertEquals(rString, rCharSeq)
    }

    @Test
    fun toString_includes_class_and_identity_hex() {
        val obj = Box(42)
        val ref = Ref(obj)
        val hex = platformIdentityHashCode(obj).toUInt().toString(16)
        val s = ref.toString()

        assertTrue(s.contains("Box"))
        assertTrue(s.contains("@$hex"))
        assertTrue(s.startsWith("Ref["))
        assertTrue(s.endsWith("]"))
    }

    @Test
    fun toString_handles_anonymous_and_arrays() {
        val anon = object {}
        val arr = IntArray(3)
        val sa = Ref(anon).toString()
        val sb = Ref(arr).toString()

        assertTrue(sa.contains("@"))
        assertTrue(sb.contains("@"))
    }
}
