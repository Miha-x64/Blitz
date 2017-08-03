package net.aquadc.blitz

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by miha on 28.03.17
 */

class ExtensionsText {

    @Test fun singleTest() {
        assertEquals(1, immutableLongSetOf(1).single())
        assertEquals(1, immutableLongSetOf(1).singleOrDefault(2))
        assertEquals(2, immutableLongSetOf().singleOrDefault(2))
    }

    @Test(expected = NoSuchElementException::class) fun singleEmptyTest() {
        immutableLongSetOf().single()
    }

    @Test(expected = IllegalArgumentException::class) fun singleFullTest() {
        immutableLongSetOf(1, 2).single()
    }

    @Test fun testFilter() {
        assertEquals(immutableLongSetOf(0, 1, 2, 3, 4, 5, 6, 7).filtered { it > 3 }, immutableLongSetOf(4, 5, 6, 7))
        assertEquals(immutableLongSetOf(0, 1, 2, 3, 4, 5, 6, 7).filteredNot { it > 3 }, immutableLongSetOf(0, 1, 2, 3))
    }

    @Test fun testSorting() {
        assertArrayEquals(mutableLongSetOf(1, 26, 34, 72, 96, 111).asSortedArray(), longArrayOf(1, 26, 34, 72, 96, 111))
    }

    @Test fun testJoining() {
        assertEquals(immutableLongSetOf(0, 1, 2, 3, 4, 5, 6, 7).joinToString(limit = 3), "0, 1, 2, ...")
    }

    @Test fun testAvgAndSum() {
        assertEquals(immutableLongSetOf(0, 1, 2, 3).average(), 1.5, 0.00000000001)
        assertEquals(immutableLongSetOf(0, 1, 2, 3).sum(), 6)
    }

    @Test fun testOperators() {
        val set = mutableLongSetOf()
        set += 1
        set += 3
        assertArrayEquals(longArrayOf(1, 3), set.asSortedArray())
        set -= 3
        assertArrayEquals(longArrayOf(1), set.asSortedArray())

        assertEquals(immutableLongSetOf(4), immutableLongSetOf() + 4)
        assertEquals(immutableLongSetOf(), immutableLongSetOf(4) - 4)
    }

    @Test fun testMap() {
        val set = immutableLongSetOf(1, 2, 3, 4, 5)
        assertEquals(arrayListOf(2L, 4L, 6L, 8L, 10L), set.map { 2 * it })
        assertArrayEquals(longArrayOf(2, 4, 6, 8, 10), set.mapToLongArray { 2 * it })
    }

    @Test fun testFold() {
        val set = immutableLongSetOf(1, 2, 3, 4, 5)
        assertEquals(10+1+2+3+4+5, set.fold(10L) { a, b -> a + b})
        assertEquals(10+1+2+3+4+5, set.foldToLong(10L) { a, b -> a + b})
    }

}