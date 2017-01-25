package net.aquadc.blitz;

import net.aquadc.blitz.impl.ImmutableLongTreeSet;
import net.aquadc.blitz.impl.MutableLongTreeSet;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by miha on 26.01.17
 */
public class MutableAndImmutableTest {

    @Test
    public void mutableTest() {
        MutableLongSet set = new MutableLongTreeSet();
        assertTrue(set.add(4));
        assertFalse(set.add(4));
        assertTrue(set.addAll(new long[] {9, 2, 8, 11}));
        assertFalse(set.addAll(new long[] {9, 2, 8, 11}));
        assertTrue(set.addAll(new MutableLongTreeSet(new long[] {63, 20})));
        assertFalse(set.addAll(new MutableLongTreeSet(new long[] {63, 20})));
        assertTrue(set.addAll(ImmutableLongTreeSet.from(new long[] {64, 201})));
        assertFalse(set.addAll(ImmutableLongTreeSet.from(new long[] {64, 201})));
        assertEquals(9, set.size());

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
    }

    @Test
    public void immutableTest() {
        // todo!
    }

}
