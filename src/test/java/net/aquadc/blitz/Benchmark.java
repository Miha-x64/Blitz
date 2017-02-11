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

    private static final int ITEMS = 10_000;

    @Before
    public void hang() throws Exception {
        Thread.sleep(300);
    }

    @Test
    public void mlTreeSetContains() {
        mlsContains(new MutableLongTreeSet());
    }
    @Test
    public void mlHashSetContains() {
        mlsContains(new MutableLongHashSet());
    }

    private void mlsContains(MutableLongSet set) {
        populateMls(set, ITEMS, "reads".hashCode());
        Random r = new Random("contains".hashCode());
        System.gc();
        long nanos = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            set.contains(r.nextLong());
        }
        showTime(System.nanoTime() - nanos, "MutableLongSet of " + ITEMS + " elements, 1 000 000 reads");
    }

    @Test
    public void tsContains() {
        Set<Long> set = new TreeSet<>();
        runContainsTestOnGenericSet(set);
    }

    @Test
    public void hsContains() {
        Set<Long> set = new HashSet<>();
        runContainsTestOnGenericSet(set);
    }

    private void runContainsTestOnGenericSet(Set<Long> set) {
        populateGenericSetOfLongs(set, ITEMS, "reads".hashCode());
        Random r = new Random("contains".hashCode());
        System.gc();
        long nanos = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            set.contains(r.nextLong());
        }
        showTime(System.nanoTime() - nanos, set.getClass().getSimpleName() + " of " + ITEMS + " elements, 1 000 000 reads");
    }

    private void populateMls(MutableLongSet set, int size, int seed) {
        Random r = new Random(seed);
        long nanos = System.nanoTime();
        while (set.size() < size) {
            set.add(r.nextLong());
        }
        showTime(System.nanoTime() - nanos, "MutableLongTreeSet: " + size + " inserts");
    }
    private void populateGenericSetOfLongs(Set<Long> emptySet, int size, int seed) {
        Random r = new Random(seed);
        long nanos = System.nanoTime();
        while (emptySet.size() < size) {
            emptySet.add(r.nextLong());
        }
        showTime(System.nanoTime() - nanos, emptySet.getClass().getSimpleName() + ": " + size + " inserts");
    }

    private void showTime(long nanos, String tag) {
        System.out.println(tag + ": " + (nanos / 1000 / 1000.0) + " ms");
    }

}
