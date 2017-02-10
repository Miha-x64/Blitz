package net.aquadc.blitz;

import net.aquadc.blitz.impl.ImmutableLongTreeSet;
import net.aquadc.blitz.impl.MutableLongTreeSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by miha on 26.01.17
 */
public class InstantiationTest {

    @Test
    public void testConstructors() {
        MutableLongTreeSet set = new MutableLongTreeSet();
        assertEquals(0, set.size());
        set.add(10);
        assertEquals(1, set.size());
        assertEquals(10, set.get(0));

        set = new MutableLongTreeSet(10);
        assertEquals(0, set.size());
        set.add(-4);
        assertEquals(-4, set.get(0));
        assertEquals(1, set.size());

        set = new MutableLongTreeSet(new long[] {});
        assertEquals(0, set.size());
        set.add(16);
        assertEquals(1, set.size());
        assertEquals(16, set.get(0));

        set = new MutableLongTreeSet(new long[] {100});
        assertEquals(1, set.size());
        assertEquals(100, set.get(0));

        set = new MutableLongTreeSet(new long[] {1, 2, 3});
        assertEquals(3, set.size());

        set = new MutableLongTreeSet(set);
        assertEquals(3, set.size());
    }

    @Test
    public void testFactories() {
        assertEquals(0, ImmutableLongTreeSet.empty().size());
        assertSame(ImmutableLongTreeSet.empty(), ImmutableLongTreeSet.from(ImmutableLongTreeSet.empty()));
        assertEquals(1, ImmutableLongTreeSet.from(new long[1]).size());
        assertEquals(2, ImmutableLongTreeSet.from(new MutableLongTreeSet(new long[] {1, 2})).size());
    }

}
