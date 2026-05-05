// SPDX-License-Identifier: LicenseRef-Wabbit-Public-Test-License-1.1

package one.wabbit.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class IdentityHelpersTest {
    @Test
    fun distinctByIdentity_works() {
        val a1 = charArrayOf('a').concatToString()
        val a2 = charArrayOf('a').concatToString()
        val xs = listOf(a1, a2, a1)
        val ys = xs.distinctByIdentity()
        assertEquals(listOf(a1, a2), ys)
        assertNotSame(a1, a2)
        assertEquals(a1, a2)
    }

    @Test
    fun identityMap_uses_reference_identity() {
        val a1 = charArrayOf('a').concatToString()
        val a2 = charArrayOf('a').concatToString()
        val map = identityMap<String, Int>()

        map[a1] = 1

        assertEquals(1, map[a1])
        assertEquals(null, map[a2])
        assertTrue(map.containsKey(a1))
        assertFalse(map.containsKey(a2))
    }

    @Test
    fun identitySet_remove_uses_reference_identity() {
        val a1 = charArrayOf('a').concatToString()
        val a2 = charArrayOf('a').concatToString()
        val set = identitySetOf(a1, a2)

        assertTrue(set.remove(a1))
        assertFalse(set.contains(a1))
        assertTrue(set.contains(a2))
    }
}
