package net.aquadc.blitz;

import net.aquadc.blitz.impl.ImmutableLongTreeSet;
import net.aquadc.blitz.impl.MutableLongTreeSet;
import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by miha on 28.01.17
 */
public class IteratorsTest {

    @Test
    public void immutableIteratorTest() {
        ImmutableLongSet set = ImmutableLongTreeSet.from(new long[] {0, 1, 2, 3, 4, 5, 6, 7});
        LongIterator itr = set.iterator();
        MutableLongSet receiver = new MutableLongTreeSet(8);
        while (itr.hasNext()) {
            long val = itr.next();
            if (val != 4)
                receiver.add(val);
        }

        assertArrayEquals(new long[] {0, 1, 2, 3, 5, 6, 7}, receiver.copyToArray());
        try {
            itr.next();
            assertTrue(false);
        } catch (NoSuchElementException e) {
            // ok
        }
    }

    @Test
    public void mutableIteratorTest() {
        MutableLongSet set = new MutableLongTreeSet(new long[] {0, 1, 2, 3, 4, 5, 6, 7});
        for (MutableLongIterator itr = set.iterator(); itr.hasNext();) {
            long val = itr.next();
            if (val == 4) {
                itr.remove();
            }
        }

        assertArrayEquals(new long[] {0, 1, 2, 3, 5, 6, 7}, set.copyToArray());
    }

    @Test(expected = ConcurrentModificationException.class)
    public void concurrentModTest() {
        MutableLongSet set = new MutableLongTreeSet(new long[] {0, 1, 2, 3, 4, 5, 6, 7});
        MutableLongIterator itr = set.iterator();
        set.remove(0);
        itr.next();
    }

}
