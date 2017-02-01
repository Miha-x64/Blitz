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
public class MutableLongTreeSetTest {

    @Test
    public void getTest() {
        assertEquals(200700, new MutableLongTreeSet(new long[] {200700}).get(0));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getThrowsTest0() {
        new MutableLongTreeSet().get(-1);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getThrowsTest1() {
        new MutableLongTreeSet().get(0);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getThrowsTest2() {
        new MutableLongTreeSet().get(1);
    }

    @Test
    public void indexOfTest() {
        assertEquals(-1, new MutableLongTreeSet().indexOf(1));
        assertEquals(0, new MutableLongTreeSet(new long[] {1}).indexOf(1));
        assertEquals(1, new MutableLongTreeSet(new long[] {1, 2}).indexOf(2));
        assertEquals(1, new MutableLongTreeSet(new long[] {2, 1}).indexOf(2)); // it'll be sorted
    }

    @Test
    public void containsTest() {
        assertFalse(new MutableLongTreeSet().contains(1));
        assertFalse(new MutableLongTreeSet(new long[] {31}).contains(32));
        assertTrue(new MutableLongTreeSet(new long[] {31}).contains(31));
        assertTrue(new MutableLongTreeSet(new long[] {0, 1, 2, -6, 11}).contains(2));
        assertFalse(new MutableLongTreeSet(new long[] {0, 1, 2, -6, 11}).contains(24));
    }

    @Test
    public void containsAll0Test() {
        assertTrue(new MutableLongTreeSet().containsAll(new long[0]));
        assertTrue(new MutableLongTreeSet(new long[] {}).containsAll(new long[] {}));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new long[] {6}));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new long[] {9}));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new long[] {24}));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new long[] {6, 9}));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new long[] {6, 24}));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new long[] {9, 24}));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new long[] {9, 24, 6}));
        assertFalse(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new long[] {9, 24, 6, 18}));
        assertFalse(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new long[] {18}));
    }

    @Test
    public void containsAll1Test() {
        assertTrue(new MutableLongTreeSet().containsAll(new long[0]));
        assertTrue(new MutableLongTreeSet(new long[] {}).containsAll(new MutableLongTreeSet()));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new MutableLongTreeSet(new long[] {6})));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new MutableLongTreeSet(new long[] {9})));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new MutableLongTreeSet(new long[] {24})));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(new MutableLongTreeSet(new long[] {6, 9})));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {6, 24})));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {9, 24})));
        assertTrue(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {9, 24, 6})));
        assertFalse(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {9, 24, 6, 18})));
        assertFalse(new MutableLongTreeSet(new long[] {6, 9, 24}).containsAll(ImmutableLongTreeSet.from(new long[] {18})));
    }

    @Test
    public void containsAtLeastOne0Test() {
        assertFalse(new MutableLongTreeSet().containsAny(new long[] {})); // Surprising? :)
        assertFalse(new MutableLongTreeSet().containsAny(new long[] {0}));
        assertTrue(new MutableLongTreeSet(new long[] {0}).containsAny(new long[] {0}));
        assertTrue(new MutableLongTreeSet(new long[] {0, 1}).containsAny(new long[] {0}));
        assertTrue(new MutableLongTreeSet(new long[] {0, 1}).containsAny(new long[] {5, 8, 1}));
        assertFalse(new MutableLongTreeSet(new long[] {0, 1}).containsAny(new long[] {5, 8}));
    }

    @Test
    public void containsAtLeastOne1Test() {
        assertFalse(new MutableLongTreeSet().containsAny(new MutableLongTreeSet()));
        assertFalse(new MutableLongTreeSet().containsAny(new MutableLongTreeSet(new long[] {0})));
        assertTrue(new MutableLongTreeSet(new long[] {0}).containsAny(new MutableLongTreeSet(new long[] {0})));
        assertTrue(new MutableLongTreeSet(new long[] {0, 1}).containsAny(new MutableLongTreeSet(new long[] {0})));
        assertTrue(new MutableLongTreeSet(new long[] {0, 1}).containsAny(new MutableLongTreeSet(new long[] {5, 8, 1})));
        assertFalse(new MutableLongTreeSet(new long[] {0, 1}).containsAny(new MutableLongTreeSet(new long[] {5, 8})));
    }

}
