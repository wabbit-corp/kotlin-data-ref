package one.wabbit.data

import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.test.*

class StableNameTest {

    @Test
    fun same_object_same_name() {
        val o = Any()
        val a = StableName.of(o)
        val b = StableName.of(o)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun different_objects_different_names_even_if_equal_by_value() {
        val x1 = Box(7)
        val x2 = Box(7)
        val a = StableName.of(x1)
        val b = StableName.of(x2)
        assertNotEquals(a, b)
    }

    @Test
    fun null_collapses_to_single_token() {
        val a = StableName.of<Any?>(null)
        val b = StableName.of<Any?>(null)
        assertEquals(a, b)
        assertEquals(0, a.hashCode())
        assertEquals("StableName(0)", a.toString())
    }

    @Test
    fun identity_overrides_value_equality_for_strings() {
        val s1 = String(charArrayOf('h','i')) // distinct instance, not a literal
        val s2 = String(charArrayOf('h','i'))
        assertNotSame(s1, s2)
        assertEquals(s1, s2) // value-equal
        val n1 = StableName.of(s1)
        val n2 = StableName.of(s2)
        assertNotEquals(n1, n2) // identity wins
    }

    @Test
    fun usable_as_map_keys() {
        val o = Any()
        val map = HashMap<StableName<Any>, String>()
        val k1 = StableName.of(o)
        map[k1] = "hit"
        val k2 = StableName.of(o)
        assertEquals("hit", map[k2])
    }

    @Test
    fun does_not_keep_referent_alive_best_effort() {
        var obj: Any? = Any()
        val weak = WeakReference(obj)
        val token = StableName.of(obj!!) // create a name, drop strong refs to obj
        obj = null

        // Try to encourage GC without being precious about it.
        repeat(100) {
            System.gc()
            if (weak.get() == null) return@repeat
            Thread.sleep(5)
        }
        StableName.sweep()
        assertNull(weak.get(), "Referent should be collectible; StableName must not retain it")
        // Token remains usable and comparable
        assertEquals(token, token)
    }

    @Test
    fun concurrent_name_requests_canonicalize_to_one_token() {
        val o = Any()
        val set = Collections.synchronizedSet(mutableSetOf<StableName<Any>>())
        val pool = Executors.newFixedThreadPool(8)
        val start = CountDownLatch(1)
        repeat(8) {
            pool.submit {
                start.await()
                repeat(10_000) { set += StableName.of(o) }
            }
        }
        start.countDown()
        pool.shutdown()
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS))
        assertEquals(1, set.size)
    }

    @Test
    fun variance_works() {
        val s = String(charArrayOf('x'))
        val a: StableName<String> = StableName.of(s)
        val b: StableName<CharSequence> = StableName.of(s)
        assertEquals(a, b)
    }
}
