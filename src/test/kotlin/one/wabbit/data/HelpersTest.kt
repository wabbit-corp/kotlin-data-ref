package one.wabbit.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class IdentityHelpersTest {
    @Test fun distinctByIdentity_works() {
        val a1 = String(charArrayOf('a'))
        val a2 = String(charArrayOf('a'))
        val xs = listOf(a1, a2, a1)
        val ys = xs.distinctByIdentity()
        assertEquals(listOf(a1, a2), ys)
        assertNotSame(a1, a2)
        assertEquals(a1, a2) // value equal, identity different
    }
}
