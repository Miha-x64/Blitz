package net.aquadc.blitz;

import net.aquadc.blitz.impl.ImmutableLongTreeSet;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by miha on 26.01.17
 */
public class ImmutableLongTreeSetTest {
// copy of Mutable*, keep synced
    @Test
    public void getTest() {
        assertEquals(200700, ImmutableLongTreeSet.from(new long[] {200700}).get(0));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getThrowsTest0() {
        ImmutableLongTreeSet.empty().get(-1);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getThrowsTest1() {
        ImmutableLongTreeSet.empty().get(0);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getThrowsTest2() {
        ImmutableLongTreeSet.empty().get(1);
    }

    @Test
    public void indexOfTest() {
        assertEquals(-1, ImmutableLongTreeSet.empty().indexOf(1));
        assertEquals(0, ImmutableLongTreeSet.from(new long[] {1}).indexOf(1));
        assertEquals(1, ImmutableLongTreeSet.from(new long[] {1, 2}).indexOf(2));
        assertEquals(1, ImmutableLongTreeSet.from(new long[] {2, 1}).indexOf(2)); // it'll be sorted
    }

    @Test
    public void containsTest() {
        assertFalse(ImmutableLongTreeSet.empty().contains(1));
        assertFalse(ImmutableLongTreeSet.from(new long[] {31}).contains(32));
        assertTrue(ImmutableLongTreeSet.from(new long[] {31}).contains(31));
        assertTrue(ImmutableLongTreeSet.from(new long[] {0, 1, 2, -6, 11}).contains(2));
        assertFalse(ImmutableLongTreeSet.from(new long[] {0, 1, 2, -6, 11}).contains(24));
    }

    @Test
    public void containsAll0Test() {
        assertTrue(ImmutableLongTreeSet.empty().containsAll(new long[0]));
        assertTrue(ImmutableLongTreeSet.from(new long[] {}).containsAll(new long[] {}));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(new long[] {6}));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(new long[] {9}));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(new long[] {24}));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(new long[] {6, 9}));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(new long[] {6, 24}));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(new long[] {9, 24}));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(new long[] {9, 24, 6}));
        assertFalse(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(new long[] {9, 24, 6, 18}));
        assertFalse(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(new long[] {18}));
    }

    @Test
    public void containsAll1Test() {
        assertTrue(ImmutableLongTreeSet.empty().containsAll(new long[0]));
        assertTrue(ImmutableLongTreeSet.from(new long[] {}).containsAll(ImmutableLongTreeSet.empty()));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {6})));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {9})));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {24})));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {6, 9})));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {6, 24})));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {9, 24})));
        assertTrue(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {9, 24, 6})));
        assertFalse(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {9, 24, 6, 18})));
        assertFalse(ImmutableLongTreeSet.from(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {18})));
    }

    @Test
    public void containsAtLeastOne0Test() {
        assertFalse(ImmutableLongTreeSet.empty().containsAny(new long[] {})); // Surprising? :)
        assertFalse(ImmutableLongTreeSet.empty().containsAny(new long[] {0}));
        assertTrue(ImmutableLongTreeSet.from(new long[] {0}).containsAny(new long[] {0}));
        assertTrue(ImmutableLongTreeSet.from(new long[] {0, 1}).containsAny(new long[] {0}));
        assertTrue(ImmutableLongTreeSet.from(new long[] {0, 1}).containsAny(new long[] {5, 8, 1}));
        assertFalse(ImmutableLongTreeSet.from(new long[] {0, 1}).containsAny(new long[] {5, 8}));
    }

    @Test
    public void containsAtLeastOne1Test() {
        assertFalse(ImmutableLongTreeSet.empty().containsAny(ImmutableLongTreeSet.empty()));
        assertFalse(ImmutableLongTreeSet.empty().containsAny(ImmutableLongTreeSet.from(new long[] {0})));
        assertTrue(ImmutableLongTreeSet.from(new long[] {0}).containsAny(ImmutableLongTreeSet.from(new long[] {0})));
        assertTrue(ImmutableLongTreeSet.from(new long[] {0, 1}).containsAny(ImmutableLongTreeSet.from(new long[] {0})));
        assertTrue(ImmutableLongTreeSet.from(new long[] {0, 1}).containsAny(ImmutableLongTreeSet.from(new long[] {5, 8, 1})));
        assertFalse(ImmutableLongTreeSet.from(new long[] {0, 1}).containsAny(ImmutableLongTreeSet.from(new long[] {5, 8})));
    }

    // own Immutable*'s  tests

    @Test
    public void withWithoutText() {
        ImmutableLongSet set = ImmutableLongTreeSet.empty();
        assertArrayEquals(new long[] {}, set.copyToArray());
        set = set.with(1);
        assertEquals(1, set.size());
        assertArrayEquals(new long[] {1}, set.copyToArray());
        set = set.with(2);
        assertEquals(2, set.size());
        assertArrayEquals(new long[] {1, 2}, set.copyToArray());
        set = set.with(2);
        assertEquals(2, set.size());
        assertArrayEquals(new long[] {1, 2}, set.copyToArray());
        set = set.without(1);
        assertEquals(1, set.size());
        assertArrayEquals(new long[] {2}, set.copyToArray());
        set = set.without(2);
        assertEquals(0, set.size());
        assertArrayEquals(new long[] {}, set.copyToArray());

        assertSame(set, ImmutableLongTreeSet.empty());
    }

}
