package net.aquadc.blitz;

import org.junit.Before;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by miha on 02.02.17
 */
public class Benchmark {

    @Before
    public void hang() throws Exception {
        Thread.sleep(300);
    }

    /*@Test
    public void memoryBenchmark() throws Exception {
//        Instrumentation instr = (Instrumentation)
//                Class.forName("InstrumentationProvider").getMethod("getInstrumentation").invoke(null, (Object[]) null);

        int seed = "memory".hashCode();
        System.out.println("# Memory");
        for (int items : new int[] {0, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10_000, 20_000, 50_000, 100_000}) {
            System.out.println("## " + items + " items");
            MutableLongSet set = new MutableLongTreeSet();
            populateMls(set, items, seed, false);
//            showMemory(instr, set);

            set = new MutableLongHashSet();
            populateMls(set, items, seed, false);
//            showMemory(instr, set);

            Set<Long> genericSet = new TreeSet<>();
            populateGenericSetOfLongs(genericSet, items, seed, false);
//            showMemory(instr, genericSet);

            genericSet = new HashSet<>();
            populateGenericSetOfLongs(genericSet, items, seed, false);
//            showMemory(instr, genericSet);
        }
    }*/

    private void showMemory(Instrumentation instr, Object object) {
        System.out.println(object.getClass().getSimpleName() + " size: " + getObjectDeepSize(instr, object, null));
    }

    private long getObjectDeepSize(Instrumentation instr, Object object, Set<Object> exclude) {
        if (exclude == null) {
            exclude = new HashSet<>();
        }

        if (!exclude.add(object)) {
//            System.out.println("skipping " + object);
            return 0; // already calculated
        }
        Class klass = object.getClass();
        if (klass == Class.class || klass.isEnum()) {
            return 0;
        }
        if (klass.isArray()) {
            if (klass.getComponentType().isPrimitive()) {
                // primitive array size is straightforward
                System.out.println(object + " is a primitive array, bytes: " + instr.getObjectSize(object));
                return instr.getObjectSize(object);
            }
            // calculate object array deep size
            long deep = getArrayDeepSize(instr, (Object[]) object, exclude);
            System.out.println(object + " is object array, bytes: " + deep);
            return deep;
        }

        // calculate object deep size
        long size = instr.getObjectSize(object);
        Field[] fields = klass.getDeclaredFields();
//        System.out.println(object + " is " + klass);
        for (Field f : fields) {
            if ((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) continue;
            Class type = f.getType();
            if (type.isPrimitive()) continue;

            try {
                f.setAccessible(true);
                Object o = f.get(object);
//                System.out.println(f + " = " + o);
                if (o == null) continue;

                size += getObjectDeepSize(instr, o, exclude);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }
        return size;
    }

    private long getArrayDeepSize(Instrumentation instr, Object[] array, Set<Object> exclude) {
        long size = instr.getObjectSize(array);
        for (Object element : array) {
            if (element == null) continue;

            size += getObjectDeepSize(instr, array, exclude);
        }
        return size;
    }

    /*@Test
    public void performanceBenchmark() {
        System.out.println("# insertions");
        int seed = "insertions".hashCode();
        for (int items : new int[] {20, 50, 100, 200, 500, 1000, 2000, 5000, 10_000, 20_000, 50_000, 100_000}) {
            System.out.println("## " + items + " items");
            populateMls(new MutableLongTreeSet(), items, seed, true);
            populateMls(new MutableLongHashSet(), items, seed, true);
            populateGenericSetOfLongs(new TreeSet<Long>(), items, seed, true);
            populateGenericSetOfLongs(new HashSet<Long>(), items, seed, true);
        }

        System.out.println("# searches");
        for (int items : new int[] {20, 50, 100, 200, 500, 1000, 2000, 5000, 10_000, 20_000, 50_000, 100_000}) {
            System.out.println("## " + items + " items");
            mlsContains(new MutableLongTreeSet(), items);
            mlsContains(new MutableLongHashSet(), items);
            runContainsTestOnGenericSet(new TreeSet<Long>(), items);
            runContainsTestOnGenericSet(new HashSet<Long>(), items);
        }
    }*/

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
