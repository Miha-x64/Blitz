package net.aquadc.blitz;

import net.aquadc.blitz.impl.ImmutableLongTreeSet;
import net.aquadc.blitz.impl.MutableLongHashSet;
import net.aquadc.blitz.impl.MutableLongTreeSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by miha on 26.01.17
 */
public class MutableTest {

    @Test
    public void mutableLongTreeSet() {
        mutableTest(new MutableLongTreeSet());
    }
    @Test
    public void mutableLongHashSet() {
        mutableTest(new MutableLongHashSet());
    }

    private void mutableTest(MutableLongSet set) {
        assertTrue(set.add(4));
        assertFalse(set.add(4));
        assertTrue(set.addAll(new long[] {9, 2, 8, 11}));
        assertFalse(set.addAll(new long[] {9, 2, 8, 11}));
        assertTrue(set.addAll(new MutableLongTreeSet(new long[] {63, 20})));
        assertFalse(set.addAll(new MutableLongTreeSet(new long[] {63, 20})));
        assertTrue(set.addAll(ImmutableLongTreeSet.from(new long[] {64, 201})));
        assertFalse(set.addAll(ImmutableLongTreeSet.from(new long[] {64, 201})));
        assertEquals(9, set.size());

        assertEquals(+1, set.addOrRemove(91));
        assertEquals(+1, set.addOrRemove(92));
        assertEquals(-1, set.addOrRemove(92));
        assertEquals(-1, set.addOrRemove(91));

        assertTrue(set.remove(2));
        assertEquals(8, set.size());
        assertFalse(set.remove(2));

        assertTrue(set.removeAll(new long[] {20, 201}));
        assertFalse(set.removeAll(new long[] {20, 201}));
        assertEquals(6, set.size());

        assertTrue(set.removeAll(new long[] {63 /*rm*/, 100500/*skip*/}));
        assertEquals(5, set.size());

        // [4, 8, 9, 11, 64]

        assertFalse(set.retainAll(new long[] {1, 2, 4, 8, 9, 10, 11, 69, 64, 33}));
        assertTrue(set.retainAll(new long[] {1, 2, 4, 8, 10, 11, 69, 64, 33})); // rm 9
        assertEquals(4, set.size());

        // [4, 8, 11, 64]

        assertFalse(set.retainAll(new MutableLongTreeSet(new long[] {65, 35, 11, 64, 8, 9, 4})));
        assertTrue(set.retainAll(new MutableLongTreeSet(new long[] {65, 35, 11, 64, 8, 9, 20}))); // rm 4
        assertEquals(3, set.size());

        set.removeAll(set.asImmutable());
        assertEquals(0, set.size());
        assertFalse(set.contains(0));
        assertTrue(set.add(0));
        assertTrue(set.contains(0));
        assertFalse(set.add(0));

        assertTrue(set.add(1));
        assertTrue(set.add(2));
        assertTrue(set.add(3));
        assertTrue(set.add(4));

        assertTrue(set.contains(0));
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
        assertFalse(set.contains(5));
        assertFalse(set.contains(6));
        assertFalse(set.contains(7));
        assertFalse(set.contains(8));

        assertTrue(set.remove(1));
        assertFalse(set.contains(1));
        assertFalse(set.remove(1));
    }

}
