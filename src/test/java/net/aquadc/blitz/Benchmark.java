package net.aquadc.blitz;

import net.aquadc.blitz.impl.MutableLongHashSet;
import net.aquadc.blitz.impl.MutableLongTreeSet;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by miha on 02.02.17
 */
public class Benchmark {

    @Before
    public void hang() throws Exception {
        Thread.sleep(300);
    }

    @Test
    public void benchmark() {
        System.out.println("# insertions");
        int seed = "insertions".hashCode();
        for (int items : new int[] {20, 50, 100, 200, 500, 1000, 2000, 5000, 10_000, 20_000, 50_000, 100_000}) {
            System.out.println("## " + items + " items");
            populateMls(new MutableLongTreeSet(), items, seed, true);
            populateMls(new MutableLongHashSet(), items, seed, true);
            populateGenericSetOfLongs(new TreeSet<>(), items, seed, true);
            populateGenericSetOfLongs(new HashSet<>(), items, seed, true);
        }

        System.out.println("# searches");
        for (int items : new int[] {20, 50, 100, 200, 500, 1000, 2000, 5000, 10_000, 20_000, 50_000, 100_000}) {
            System.out.println("## " + items + " items");
            mlsContains(new MutableLongTreeSet(), items);
            mlsContains(new MutableLongHashSet(), items);
            runContainsTestOnGenericSet(new TreeSet<>(), items);
            runContainsTestOnGenericSet(new HashSet<>(), items);
        }
    }

    private void mlsContains(MutableLongSet set, int items) {
        populateMls(set, items, "reads".hashCode(), false);
        Random r = new Random("contains".hashCode());
        System.gc();
        long nanos = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            set.contains(r.nextLong());
        }
        showTime(System.nanoTime() - nanos, set.getClass().getSimpleName() + " of " + items + " elements, 1 000 000 reads");
    }

    private void runContainsTestOnGenericSet(Set<Long> set, int items) {
        populateGenericSetOfLongs(set, items, "reads".hashCode(), false);
        Random r = new Random("contains".hashCode());
        System.gc();
        long nanos = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            set.contains(r.nextLong());
        }
        showTime(System.nanoTime() - nanos, set.getClass().getSimpleName() + " of " + items + " elements, 1 000 000 reads");
    }

    private void populateMls(MutableLongSet set, int size, int seed, boolean showTime) {
        Random r = new Random(seed);
        long nanos = System.nanoTime();
        while (set.size() < size) {
            set.add(r.nextLong());
        }
        if (showTime) {
            showTime(System.nanoTime() - nanos, set.getClass().getSimpleName() + ": " + size + " inserts");
        }
    }
    private void populateGenericSetOfLongs(Set<Long> emptySet, int size, int seed, boolean showTime) {
        Random r = new Random(seed);
        long nanos = System.nanoTime();
        while (emptySet.size() < size) {
            emptySet.add(r.nextLong());
        }
        if (showTime) {
            showTime(System.nanoTime() - nanos, emptySet.getClass().getSimpleName() + ": " + size + " inserts");
        }
    }

    private void showTime(long nanos, String tag) {
        System.out.println(tag + ": " + (nanos / 1000 / 1000.0) + " ms");
    }

}
