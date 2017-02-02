package net.aquadc.blitz;

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
    private static final Runtime RUNTIME = Runtime.getRuntime();

    @Before
    public void hang() throws Exception {
        Thread.sleep(300);
    }

    @Test
    public void mltsContains() {
        MutableLongSet set = populatedMlts(ITEMS, "reads".hashCode());
        Random r = new Random("contains".hashCode());
        System.gc();
        long jvmMemory = RUNTIME.totalMemory() - RUNTIME.freeMemory();
        long nanos = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            set.contains(r.nextLong());
        }
        showTime(System.nanoTime() - nanos, "MutableLongSet of " + ITEMS + " elements, 1 000 000 reads");
//        showMemory(jvmMemory, set);
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
        long jvmMemory = RUNTIME.totalMemory() - RUNTIME.freeMemory();
        long nanos = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            set.contains(r.nextLong());
        }
        showTime(System.nanoTime() - nanos, set.getClass().getSimpleName() + " of " + ITEMS + " elements, 1 000 000 reads");
//        showMemory(jvmMemory, set);
    }

    private MutableLongSet populatedMlts(int size, int seed) {
        MutableLongSet mls = new MutableLongTreeSet();
        Random r = new Random(seed);
        long nanos = System.nanoTime();
        while (mls.size() < size) {
            mls.add(r.nextLong());
        }
        showTime(System.nanoTime() - nanos, "MutableLongTreeSet: " + size + " inserts");
        return mls;
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

    private void showMemory(long jvmMemory, Object retainMePlease) {
//        long now = RUNTIME.totalMemory() - RUNTIME.freeMemory();
        System.gc();
        long after = RUNTIME.totalMemory() - RUNTIME.freeMemory();
//        System.out.println("Before start: " + jvmMemory/1000 + " KiB; after: " + now/1000 + " KiB; after GC: " + after/1000 + " KiB; retained object: @" + Integer.toHexString(retainMePlease.hashCode()));
        System.out.println((after - jvmMemory) + " B retained by " + retainMePlease.getClass().getSimpleName());
    }

}
