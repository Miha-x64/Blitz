package net.aquadc.blitz.impl;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by miha on 26.01.17
 */
public class LongsTest {

    @Test
    public void testRealloc() {
        long[] longs = new long[0];
        longs = Longs.reallocAndInsert(longs, 0, 4, 0, 100);
        assertArrayEquals(new long[] {100, 0, 0, 0}, longs);

        long[] reallocated = Longs.reallocAndInsert(longs, 1, 5 /* 8 */, 0, 200);
        assertNotSame(longs, reallocated);
        assertArrayEquals(new long[] {100, 0, 0, 0}, longs); // assert not changed
        assertArrayEquals(new long[] {200, 100, 0, 0, 0, 0, 0, 0}, reallocated);
    }

    @Test
    public void insertTest() {
        long[] longs = {1, 2, 3, 4, 5};
        Longs.insert(longs, 3, 3, 100500);
        assertArrayEquals(new long[] {1, 2, 3, 100500, 5}, longs); // 4 erased, it's out of specified size
        Longs.insert(longs, 0, 2, 200700);
        assertArrayEquals(new long[] {200700, 1, 2, 100500, 5}, longs);
    }

    @Test
    public void testIndexOf() {
        long[] longs = {0, 1, 2, 1, 3, 10, 4, 2, 11, 9, 1, 2};
        assertEquals(0, Longs.indexOf(longs, 0));
        assertEquals(1, Longs.indexOf(longs, 1));
        assertEquals(2, Longs.indexOf(longs, 2));
        assertEquals(4, Longs.indexOf(longs, 3));
        assertEquals(5, Longs.indexOf(longs, 10));
        assertEquals(6, Longs.indexOf(longs, 4));
        assertEquals(8, Longs.indexOf(longs, 11));
        assertEquals(9, Longs.indexOf(longs, 9));
    }

    @Test
    public void testToString() {
        assertEquals("[]", Longs.toString(new long[]{}, 0));
        assertEquals("[]", Longs.toString(new long[]{0}, 0));
        assertEquals("[]", Longs.toString(new long[]{0, 1}, 0));
        assertEquals("[0]", Longs.toString(new long[]{0, 1}, 1));
        assertEquals("[0, 1]", Longs.toString(new long[]{0, 1}, 2));
        assertEquals("[0, 1]", Longs.toString(new long[]{0, 1, 2}, 2));
        assertEquals("[0, 1, 2]", Longs.toString(new long[]{0, 1, 2}, 3));
    }

    @Test
    public void testEqual() {
        assertTrue(Longs.orderedSetsEqual(new long[0], 0, new MutableLongTreeSet()));
        assertTrue(Longs.orderedSetsEqual(new long[1], 0, new MutableLongTreeSet()));
        assertTrue(Longs.orderedSetsEqual(new long[0], 0, ImmutableLongTreeSet.empty()));
        assertTrue(Longs.orderedSetsEqual(new long[1], 0, ImmutableLongTreeSet.empty()));

        assertTrue(Longs.orderedSetsEqual(new long[1], 1, new MutableLongTreeSet(new long[] {0})));
        assertTrue(Longs.orderedSetsEqual(new long[1], 1, ImmutableLongTreeSet.from(new long[] {0})));

        assertTrue(Longs.orderedSetsEqual(new long[] {0, 0}, 1, new MutableLongTreeSet(new long[] {0, 0})));
        assertFalse(Longs.orderedSetsEqual(new long[] {0, 0}, 2, new MutableLongTreeSet(new long[] {0, 1})));
        assertTrue(Longs.orderedSetsEqual(new long[] {0, 0}, 1, ImmutableLongTreeSet.from(new long[] {0, 0})));
        assertFalse(Longs.orderedSetsEqual(new long[] {0, 0}, 2, ImmutableLongTreeSet.from(new long[] {0, 1})));
    }

    @Test
    public void testHashCode() {
        long[] array = new long[] {0, 1, 2, 3, 2, 5, 9, 7};
        Long[] Array = new Long[] {0L, 1L, 2L, 3L, 2L, 5L, 9L, 7L};
        Set<Long> set = new HashSet<>(Arrays.asList(Array));
        assertEquals(Collections.emptySet().hashCode(), new MutableLongTreeSet().hashCode());
        assertEquals(new HashSet<>().hashCode(), new MutableLongTreeSet().hashCode());
        assertEquals(set.hashCode(), new MutableLongTreeSet(array).hashCode());

        assertEquals(Collections.emptySet().hashCode(), ImmutableLongTreeSet.empty().hashCode());
        assertEquals(new HashSet<>().hashCode(), ImmutableLongTreeSet.empty().hashCode());
        assertEquals(set.hashCode(), ImmutableLongTreeSet.from(array).hashCode());
    }

}
