package net.aquadc.blitz;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by mike on 07.02.17
 */
/*public class LongHashSetTest {

//    @Test
    public void testPopulation() {
        testPopulationWith(100);
        testPopulationWith(1000);
        testPopulationWith(10_000);
        testPopulationWith(100_000);
        testPopulationWith(1_000_000);
        testPopulationWith(10_000_000);
        testPopulationWith(50_000_000);
    }

    private void testPopulationWith(int nElements) {
        System.gc();
        LongHashSet set = new LongHashSet();
        Random r = new Random("one".hashCode());
        long nanos = System.nanoTime();
        for (int i = 0; i < nElements; i++) {
            set.add(r.nextLong());
        }
        nanos = System.nanoTime() - nanos;
        populatedIn("LongHashSet", nElements, nanos);

        System.gc();
        r = new Random("one".hashCode());
        HashSet<Long> std = new HashSet<>();
        nanos = System.nanoTime();
        for (int i = 0; i < nElements; i++) {
            std.add(r.nextLong());
        }
        nanos = System.nanoTime() - nanos;
        populatedIn("HashSet<Long>", nElements, nanos);
    }
    private static void populatedIn(String name, int els, long nanos) {
        System.out.println(name + " populated with " + els + " in " + (nanos / 1000 / 1000.0) + " ms");
    }

//    @Test
    public void testContains() {
        testContainsWith(100, 100);
        testContainsWith(1_000, 100);
        testContainsWith(10_000, 100);
        testContainsWith(100_000, 100);
        testContainsWith(1_000_000, 100);

        testContainsWith(100, 10_000_000);
        testContainsWith(1_000, 10_000_000);
        testContainsWith(10_000, 10_000_000);
        testContainsWith(100_000, 10_000_000);
        testContainsWith(1_000_000, 10_000_000);
    }
    private void testContainsWith(int size, int reads) {
        Random r = new Random("two".hashCode());
        LongHashSet set = new LongHashSet();
        for (int i = 0; i < size; i++) {
            set.add(r.nextLong());
        }

        long nanos = System.nanoTime();
        for (int i = 0; i < reads; i++) {
            set.contains(r.nextLong());
        }
        nanos = System.nanoTime() - nanos;
        containsIn("LongHashSet", size, reads, nanos);

        r = new Random("two".hashCode());
        HashSet<Long> std = new HashSet<>();
        for (int i = 0; i < size; i++) {
            std.add(r.nextLong());
        }

        nanos = System.nanoTime();
        for (int i = 0; i < reads; i++) {
            std.contains(r.nextLong());
        }
        nanos = System.nanoTime() - nanos;
        containsIn("HashSet<Long>", size, reads, nanos);
    }
    private static void containsIn(String name, int els, int reads, long nanos) {
        System.out.println(reads + " reads on " + name + " of " + els + " took " + (nanos / 1000 / 1000.0) + " ms");
    }

//    @Test
    public void profile() throws Exception {
        System.gc();
        Thread.sleep(10_000);
        int nElements = 1_000_000;
        System.gc();
        LongHashSet set = new LongHashSet();
        Random r = new Random("one".hashCode());
        for (int i = 0; i < nElements; i++) {
            set.add(r.nextLong());
        } // 24 + 16 = 40 MiB
        set = null;

        Thread.sleep(5_000);
        gc();
        Thread.sleep(3_000);

        r = new Random("one".hashCode());
        HashSet<Long> std = new HashSet<>();
        for (int i = 0; i < nElements; i++) {
            std.add(r.nextLong());
        } // 32 + 24 + 8 = 64 MiB
        std = null;

        Thread.sleep(5_000);
        gc();
        Thread.sleep(3_000);
    }

    private static void gc() {
        long nanos = System.nanoTime();
        System.gc();
        nanos = System.nanoTime() - nanos;
        System.out.println("GCed in " + nanos/1000 + " us");
    }

}*/
