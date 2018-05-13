package net.aquadc.blitz;

import com.carrotsearch.hppc.LongHashSet;
import com.koloboke.collect.set.hash.HashLongSet;
import gnu.trove.set.hash.TLongHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

import java.util.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class LongHashSetsBench {

    @Param({
            "MutableLongHashSet",
            /*"TreeSet", "HashSet",*/
            /*"TLongHashSet", "HPPC_LongHashSet",
            "HPPC_RT_LongHashSet", "FastUtil_LongOpenHashSet",
            "Koloboke_HashLongSets",
            "Eclipse_LongHashSet",
            "Agrona_LongHashSet"*/
    })
    LongSetType setType;

    @Param({
            /*"20", "50", "100",
            "200", "500", "1000",*/
            "2000", /*"5000", "10000",
            "20"+"000", "50"+"000", "100"+"000"*/
    })
    int setSize;
    long[] elements;
    int currentElement;

    private MutableLongSet pSet;  // Blitz!
    private Set<Long> jSet;       // JDK
    private TLongHashSet tSet;    // Trove
    private LongHashSet hSet;     // HPPC
    private com.carrotsearch.hppcrt.sets.LongHashSet rSet; // HPPC-RT
    private LongOpenHashSet fSet; // fastutil
    private HashLongSet kSet;     // Koloboke
    private org.eclipse.collections.impl.set.mutable.primitive.LongHashSet eSet; // Eclipse
    private org.agrona.collections.LongHashSet aSet; // Agrona

    @Setup
    public void before() {
        Object set = setType.create(setSize, new Random("before".hashCode()));
        if (set instanceof MutableLongSet) {
            pSet = (MutableLongSet) set;
        } else if (set.getClass() == HashSet.class || set.getClass() == TreeSet.class) {
            // ^ avoid treating subclasses as JDK sets
            jSet = (Set<Long>) set;
        } else if (set instanceof TLongHashSet) {
            tSet = (TLongHashSet) set;
        } else if (set instanceof LongHashSet) {
            hSet = (LongHashSet) set;
        } else if (set instanceof com.carrotsearch.hppcrt.sets.LongHashSet) {
            rSet = (com.carrotsearch.hppcrt.sets.LongHashSet) set;
        } else if (set instanceof LongOpenHashSet) {
            fSet = (LongOpenHashSet) set;
        } else if (set instanceof HashLongSet) {
            kSet = (HashLongSet) set;
        } else if (set instanceof org.eclipse.collections.impl.set.mutable.primitive.LongHashSet) {
            eSet = (org.eclipse.collections.impl.set.mutable.primitive.LongHashSet) set;
        } else if (set instanceof org.agrona.collections.LongHashSet) {
            aSet = (org.agrona.collections.LongHashSet) set;
        } else {
            throw new AssertionError();
        }

        /* trace:
         * [0] is getStackTrace()
         * [1] is this method, before()
         * [2] is caller
         */
        // class name: ThisClass_benchmarkMethodName_jmhTest
        String caller = Thread.currentThread().getStackTrace()[2].getClassName().split("_")[1];
        //     ^ either "insert" or "search"

        final int elCount = 67_108_864; // cool number, a power of two
        elements = new long[elCount];
        Random r = new Random(caller.hashCode());
        for (int i = 0; i < elCount; i++) {
            elements[i] = r.nextLong();
        }
    }

    @Benchmark
    public void insert(Blackhole bh) {
        if (pSet != null) {
            bh.consume(pSet.add(elements[currentElement++]));
        } else if (jSet != null) {
            bh.consume(jSet.add(elements[currentElement++]));
        } else if (tSet != null) {
            bh.consume(tSet.add(elements[currentElement++]));
        } else if (hSet != null) {
            bh.consume(hSet.add(elements[currentElement++]));
        } else if (rSet != null) {
            bh.consume(rSet.add(elements[currentElement++]));
        } else if (fSet != null) {
            bh.consume(fSet.add(elements[currentElement++]));
        } else if (kSet != null) {
            bh.consume(kSet.add(elements[currentElement++]));
        } else if (eSet != null) {
            bh.consume(eSet.add(elements[currentElement++]));
        } else if (aSet != null) {
            bh.consume(aSet.add(elements[currentElement++]));
        } else {
            throw new AssertionError();
        }
    }

    @Benchmark
    public void search(Blackhole bh) {
        // can't allocate so many elements, just reuse existing ones
        currentElement = (currentElement + 1) & (elements.length - 1);
        if (pSet != null) {
            bh.consume(pSet.contains(elements[currentElement]));
        } else if (jSet != null) {
            bh.consume(jSet.contains(elements[currentElement]));
        } else if (tSet != null) {
            bh.consume(tSet.contains(elements[currentElement]));
        } else if (hSet != null) {
            bh.consume(hSet.contains(elements[currentElement]));
        } else if (rSet != null) {
            bh.consume(rSet.contains(elements[currentElement]));
        } else if (fSet != null) {
            bh.consume(fSet.contains(elements[currentElement]));
        } else if (kSet != null) {
            bh.consume(kSet.contains(elements[currentElement]));
        } else if (eSet != null) {
            bh.consume(eSet.contains(elements[currentElement]));
        } else if (aSet != null) {
            bh.consume(aSet.contains(elements[currentElement]));
        } else {
            throw new AssertionError();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            perf();
        } else if (args.length == 1 && "memory".equals(args[0])) {
            memory();
        } else {
            System.err.println("Unknown CLI arguments: " + Arrays.toString(args));
        }
    }

    private static void perf() throws Exception {
        new Runner(
                new OptionsBuilder()
                        .include(LongHashSetsBench.class.getSimpleName())
                        .mode(Mode.AverageTime)
                        .forks(1)
                        .timeUnit(TimeUnit.NANOSECONDS)
                        .warmupIterations(3)
                        .measurementIterations(3)
                        .build()
        ).run();
    }

    private static void memory() {
        System.out.println(VM.current().details());
        System.out.println();

        for (int size : new int[] { 20, 50, 100, 200, 500, 1000, 2_000, 5_000, 10_000, 20_000, 50_000, 100_000 }) {
            for (LongSetType type : LongSetType.values()) {
                if (type == LongSetType.HashSet || type == LongSetType.TreeSet) {
                    continue; // skip JDK non-primitive collections
                }
                Random random = new Random("deep size".hashCode());
                Object set = type.create(size, random);
                System.out.print(type);
                System.out.print('\t');
                System.out.print(size);
                System.out.print('\t');
                System.out.println(GraphLayout.parseInstance(set).totalSize());
            }
            System.out.println();
        }
    }

}

/*

JMH run results:

Benchmark                 (setSize)                 (setType)  Mode  Cnt      Score         Error  Units
LongHashSetsBench.insert         20        MutableLongHashSet  avgt    3    228.104 ±    3029.590  ns/op
LongHashSetsBench.insert         20              TLongHashSet  avgt    3    253.179 ±    3644.714  ns/op
LongHashSetsBench.insert         20          HPPC_LongHashSet  avgt    3    283.237 ±    5186.343  ns/op
LongHashSetsBench.insert         20       HPPC_RT_LongHashSet  avgt    3    204.449 ±    2369.175  ns/op
LongHashSetsBench.insert         20  FastUtil_LongOpenHashSet  avgt    3    199.843 ±    2297.515  ns/op
LongHashSetsBench.insert         20     Koloboke_HashLongSets  avgt    3    177.738 ±    1580.536  ns/op
LongHashSetsBench.insert         20       Eclipse_LongHashSet  avgt    3    195.838 ±    2589.285  ns/op
LongHashSetsBench.insert         20        Agrona_LongHashSet  avgt    3   4301.920 ±  132456.055  ns/op
LongHashSetsBench.insert         50        MutableLongHashSet  avgt    3    231.055 ±    3255.959  ns/op
LongHashSetsBench.insert         50              TLongHashSet  avgt    3    349.452 ±    6309.393  ns/op
LongHashSetsBench.insert         50          HPPC_LongHashSet  avgt    3    279.333 ±    5096.377  ns/op
LongHashSetsBench.insert         50       HPPC_RT_LongHashSet  avgt    3    204.001 ±    2347.690  ns/op
LongHashSetsBench.insert         50  FastUtil_LongOpenHashSet  avgt    3    199.369 ±    2268.126  ns/op
LongHashSetsBench.insert         50     Koloboke_HashLongSets  avgt    3    183.454 ±    1829.527  ns/op
LongHashSetsBench.insert         50       Eclipse_LongHashSet  avgt    3    196.304 ±    2619.213  ns/op
LongHashSetsBench.insert         50        Agrona_LongHashSet  avgt    3   1418.869 ±   41311.301  ns/op
LongHashSetsBench.insert        100        MutableLongHashSet  avgt    3    231.990 ±    3274.826  ns/op
LongHashSetsBench.insert        100              TLongHashSet  avgt    3    345.743 ±    6197.895  ns/op
LongHashSetsBench.insert        100          HPPC_LongHashSet  avgt    3    271.101 ±    4839.797  ns/op
LongHashSetsBench.insert        100       HPPC_RT_LongHashSet  avgt    3    204.910 ±    2364.129  ns/op
LongHashSetsBench.insert        100  FastUtil_LongOpenHashSet  avgt    3    199.620 ±    2268.295  ns/op
LongHashSetsBench.insert        100     Koloboke_HashLongSets  avgt    3    183.284 ±    1809.456  ns/op
LongHashSetsBench.insert        100       Eclipse_LongHashSet  avgt    3    196.069 ±    2611.655  ns/op
LongHashSetsBench.insert        100        Agrona_LongHashSet  avgt    3   2726.307 ±   82656.669  ns/op
LongHashSetsBench.insert        200        MutableLongHashSet  avgt    3    229.458 ±    3061.510  ns/op
LongHashSetsBench.insert        200              TLongHashSet  avgt    3    553.424 ±   12600.156  ns/op
LongHashSetsBench.insert        200          HPPC_LongHashSet  avgt    3    279.604 ±    5086.614  ns/op
LongHashSetsBench.insert        200       HPPC_RT_LongHashSet  avgt    3    203.410 ±    2324.993  ns/op
LongHashSetsBench.insert        200  FastUtil_LongOpenHashSet  avgt    3    200.184 ±    2305.484  ns/op
LongHashSetsBench.insert        200     Koloboke_HashLongSets  avgt    3    183.498 ±    1676.639  ns/op
LongHashSetsBench.insert        200       Eclipse_LongHashSet  avgt    3  21100.866 ±  662969.132  ns/op
LongHashSetsBench.insert        200        Agrona_LongHashSet  avgt    3   5321.103 ±  164665.567  ns/op
LongHashSetsBench.insert        500        MutableLongHashSet  avgt    3    231.804 ±    3248.498  ns/op
LongHashSetsBench.insert        500              TLongHashSet  avgt    3    420.914 ±    8553.437  ns/op
LongHashSetsBench.insert        500          HPPC_LongHashSet  avgt    3    279.351 ±    5102.995  ns/op
LongHashSetsBench.insert        500       HPPC_RT_LongHashSet  avgt    3    205.587 ±    2391.915  ns/op
LongHashSetsBench.insert        500  FastUtil_LongOpenHashSet  avgt    3    201.162 ±    2316.752  ns/op
LongHashSetsBench.insert        500     Koloboke_HashLongSets  avgt    3    181.368 ±    1679.942  ns/op
LongHashSetsBench.insert        500       Eclipse_LongHashSet  avgt    3   7883.064 ±  245286.630  ns/op
LongHashSetsBench.insert        500        Agrona_LongHashSet  avgt    3   1684.728 ±   49719.511  ns/op
LongHashSetsBench.insert       1000        MutableLongHashSet  avgt    3    229.134 ±    3018.903  ns/op
LongHashSetsBench.insert       1000              TLongHashSet  avgt    3    406.680 ±    8077.367  ns/op
LongHashSetsBench.insert       1000          HPPC_LongHashSet  avgt    3    282.555 ±    5192.197  ns/op
LongHashSetsBench.insert       1000       HPPC_RT_LongHashSet  avgt    3    205.280 ±    2384.377  ns/op
LongHashSetsBench.insert       1000  FastUtil_LongOpenHashSet  avgt    3    200.040 ±    2299.034  ns/op
LongHashSetsBench.insert       1000     Koloboke_HashLongSets  avgt    3    179.355 ±    1632.192  ns/op
LongHashSetsBench.insert       1000       Eclipse_LongHashSet  avgt    3    196.297 ±    2636.430  ns/op
LongHashSetsBench.insert       1000        Agrona_LongHashSet  avgt    3   2769.660 ±   84027.918  ns/op
LongHashSetsBench.insert       2000        MutableLongHashSet  avgt    3    225.043 ±    3034.400  ns/op
LongHashSetsBench.insert       2000              TLongHashSet  avgt    3    413.987 ±    8340.665  ns/op
LongHashSetsBench.insert       2000          HPPC_LongHashSet  avgt    3    275.493 ±    4965.302  ns/op
LongHashSetsBench.insert       2000       HPPC_RT_LongHashSet  avgt    3    204.647 ±    2359.236  ns/op
LongHashSetsBench.insert       2000  FastUtil_LongOpenHashSet  avgt    3    199.387 ±    2277.855  ns/op
LongHashSetsBench.insert       2000     Koloboke_HashLongSets  avgt    3    183.675 ±    1821.845  ns/op
LongHashSetsBench.insert       2000       Eclipse_LongHashSet  avgt    3  39183.624 ± 1234368.590  ns/op
LongHashSetsBench.insert       2000        Agrona_LongHashSet  avgt    3   1800.240 ±   53383.140  ns/op
LongHashSetsBench.insert       5000        MutableLongHashSet  avgt    3    230.243 ±    3204.546  ns/op
LongHashSetsBench.insert       5000              TLongHashSet  avgt    3    288.102 ±    4386.828  ns/op
LongHashSetsBench.insert       5000          HPPC_LongHashSet  avgt    3    283.323 ±    5226.766  ns/op
LongHashSetsBench.insert       5000       HPPC_RT_LongHashSet  avgt    3    203.506 ±    2324.825  ns/op
LongHashSetsBench.insert       5000  FastUtil_LongOpenHashSet  avgt    3    200.234 ±    2298.252  ns/op
LongHashSetsBench.insert       5000     Koloboke_HashLongSets  avgt    3    179.390 ±    1645.063  ns/op
LongHashSetsBench.insert       5000       Eclipse_LongHashSet  avgt    3    195.097 ±    2605.354  ns/op
LongHashSetsBench.insert       5000        Agrona_LongHashSet  avgt    3   2169.280 ±   65047.137  ns/op
LongHashSetsBench.insert      10000        MutableLongHashSet  avgt    3    230.998 ±    3248.816  ns/op
LongHashSetsBench.insert      10000              TLongHashSet  avgt    3    286.371 ±    4341.710  ns/op
LongHashSetsBench.insert      10000          HPPC_LongHashSet  avgt    3    264.534 ±    4627.767  ns/op
LongHashSetsBench.insert      10000       HPPC_RT_LongHashSet  avgt    3    205.880 ±    2398.253  ns/op
LongHashSetsBench.insert      10000  FastUtil_LongOpenHashSet  avgt    3    199.463 ±    2275.350  ns/op
LongHashSetsBench.insert      10000     Koloboke_HashLongSets  avgt    3    181.845 ±    1706.935  ns/op
LongHashSetsBench.insert      10000       Eclipse_LongHashSet  avgt    3    193.549 ±    2569.699  ns/op
LongHashSetsBench.insert      10000        Agrona_LongHashSet  avgt    3   2581.516 ±   78083.189  ns/op
LongHashSetsBench.insert      20000        MutableLongHashSet  avgt    3    229.220 ±    3196.353  ns/op
LongHashSetsBench.insert      20000              TLongHashSet  avgt    3    280.384 ±    3967.751  ns/op
LongHashSetsBench.insert      20000          HPPC_LongHashSet  avgt    3    270.792 ±    4807.629  ns/op
LongHashSetsBench.insert      20000       HPPC_RT_LongHashSet  avgt    3    204.398 ±    2360.670  ns/op
LongHashSetsBench.insert      20000  FastUtil_LongOpenHashSet  avgt    3    199.115 ±    2254.016  ns/op
LongHashSetsBench.insert      20000     Koloboke_HashLongSets  avgt    3    186.298 ±    1903.284  ns/op
LongHashSetsBench.insert      20000       Eclipse_LongHashSet  avgt    3    198.326 ±    2702.804  ns/op
LongHashSetsBench.insert      20000        Agrona_LongHashSet  avgt    3    157.861 ±    1795.450  ns/op
LongHashSetsBench.insert      50000        MutableLongHashSet  avgt    3    232.740 ±    3416.711  ns/op
LongHashSetsBench.insert      50000              TLongHashSet  avgt    3  11489.517 ±  358183.483  ns/op
LongHashSetsBench.insert      50000          HPPC_LongHashSet  avgt    3    303.045 ±    5785.233  ns/op
LongHashSetsBench.insert      50000       HPPC_RT_LongHashSet  avgt    3    204.732 ±    2418.892  ns/op
LongHashSetsBench.insert      50000  FastUtil_LongOpenHashSet  avgt    3    199.778 ±    2352.221  ns/op
LongHashSetsBench.insert      50000     Koloboke_HashLongSets  avgt    3    176.468 ±    1551.781  ns/op
LongHashSetsBench.insert      50000       Eclipse_LongHashSet  avgt    3    198.046 ±    2759.991  ns/op
LongHashSetsBench.insert      50000        Agrona_LongHashSet  avgt    3    157.010 ±    1786.464  ns/op
LongHashSetsBench.insert     100000        MutableLongHashSet  avgt    3    234.961 ±    3462.820  ns/op
LongHashSetsBench.insert     100000              TLongHashSet  avgt    3    238.344 ±    3271.362  ns/op
LongHashSetsBench.insert     100000          HPPC_LongHashSet  avgt    3    294.848 ±    5597.314  ns/op
LongHashSetsBench.insert     100000       HPPC_RT_LongHashSet  avgt    3    204.853 ±    2421.759  ns/op
LongHashSetsBench.insert     100000  FastUtil_LongOpenHashSet  avgt    3    199.143 ±    2329.590  ns/op
LongHashSetsBench.insert     100000     Koloboke_HashLongSets  avgt    3    172.008 ±    1435.543  ns/op
LongHashSetsBench.insert     100000       Eclipse_LongHashSet  avgt    3    192.908 ±    2611.384  ns/op
LongHashSetsBench.insert     100000        Agrona_LongHashSet  avgt    3    157.116 ±    1754.219  ns/op

LongHashSetsBench.search         20        MutableLongHashSet  avgt    3     33.574 ±       1.900  ns/op
LongHashSetsBench.search         20              TLongHashSet  avgt    3     45.314 ±       1.194  ns/op
LongHashSetsBench.search         20          HPPC_LongHashSet  avgt    3     37.829 ±       0.727  ns/op
LongHashSetsBench.search         20       HPPC_RT_LongHashSet  avgt    3     35.824 ±       1.793  ns/op
LongHashSetsBench.search         20  FastUtil_LongOpenHashSet  avgt    3     35.215 ±       2.684  ns/op
LongHashSetsBench.search         20     Koloboke_HashLongSets  avgt    3     36.904 ±       0.628  ns/op
LongHashSetsBench.search         20       Eclipse_LongHashSet  avgt    3     33.785 ±       2.122  ns/op
LongHashSetsBench.search         20        Agrona_LongHashSet  avgt    3     22.021 ±       1.102  ns/op
LongHashSetsBench.search         50        MutableLongHashSet  avgt    3     29.043 ±       0.393  ns/op
LongHashSetsBench.search         50              TLongHashSet  avgt    3     37.682 ±       0.855  ns/op
LongHashSetsBench.search         50          HPPC_LongHashSet  avgt    3     28.225 ±       2.060  ns/op
LongHashSetsBench.search         50       HPPC_RT_LongHashSet  avgt    3     29.160 ±       3.432  ns/op
LongHashSetsBench.search         50  FastUtil_LongOpenHashSet  avgt    3     26.538 ±       1.333  ns/op
LongHashSetsBench.search         50     Koloboke_HashLongSets  avgt    3     28.217 ±       1.672  ns/op
LongHashSetsBench.search         50       Eclipse_LongHashSet  avgt    3     36.440 ±      12.299  ns/op
LongHashSetsBench.search         50        Agrona_LongHashSet  avgt    3     26.220 ±       0.236  ns/op
LongHashSetsBench.search        100        MutableLongHashSet  avgt    3     30.794 ±       0.711  ns/op
LongHashSetsBench.search        100              TLongHashSet  avgt    3     37.209 ±       3.042  ns/op
LongHashSetsBench.search        100          HPPC_LongHashSet  avgt    3     28.204 ±       0.966  ns/op
LongHashSetsBench.search        100       HPPC_RT_LongHashSet  avgt    3     27.915 ±       0.792  ns/op
LongHashSetsBench.search        100  FastUtil_LongOpenHashSet  avgt    3     26.186 ±       1.589  ns/op
LongHashSetsBench.search        100     Koloboke_HashLongSets  avgt    3     28.208 ±       2.942  ns/op
LongHashSetsBench.search        100       Eclipse_LongHashSet  avgt    3     37.245 ±       4.434  ns/op
LongHashSetsBench.search        100        Agrona_LongHashSet  avgt    3     27.597 ±       1.274  ns/op
LongHashSetsBench.search        200        MutableLongHashSet  avgt    3     30.015 ±       1.994  ns/op
LongHashSetsBench.search        200              TLongHashSet  avgt    3     46.176 ±       2.186  ns/op
LongHashSetsBench.search        200          HPPC_LongHashSet  avgt    3     28.123 ±       1.192  ns/op
LongHashSetsBench.search        200       HPPC_RT_LongHashSet  avgt    3     29.377 ±       1.697  ns/op
LongHashSetsBench.search        200  FastUtil_LongOpenHashSet  avgt    3     25.227 ±       1.364  ns/op
LongHashSetsBench.search        200     Koloboke_HashLongSets  avgt    3     27.726 ±       2.093  ns/op
LongHashSetsBench.search        200       Eclipse_LongHashSet  avgt    3     38.063 ±       2.384  ns/op
LongHashSetsBench.search        200        Agrona_LongHashSet  avgt    3     27.517 ±       0.667  ns/op
LongHashSetsBench.search        500        MutableLongHashSet  avgt    3     34.751 ±       1.786  ns/op
LongHashSetsBench.search        500              TLongHashSet  avgt    3     48.439 ±       5.447  ns/op
LongHashSetsBench.search        500          HPPC_LongHashSet  avgt    3     33.213 ±       2.064  ns/op
LongHashSetsBench.search        500       HPPC_RT_LongHashSet  avgt    3     34.102 ±       1.453  ns/op
LongHashSetsBench.search        500  FastUtil_LongOpenHashSet  avgt    3     31.370 ±       0.987  ns/op
LongHashSetsBench.search        500     Koloboke_HashLongSets  avgt    3     32.970 ±       1.736  ns/op
LongHashSetsBench.search        500       Eclipse_LongHashSet  avgt    3     40.916 ±       2.182  ns/op
LongHashSetsBench.search        500        Agrona_LongHashSet  avgt    3     31.733 ±       1.435  ns/op
LongHashSetsBench.search       1000        MutableLongHashSet  avgt    3     35.257 ±       3.409  ns/op
LongHashSetsBench.search       1000              TLongHashSet  avgt    3     47.844 ±       4.461  ns/op
LongHashSetsBench.search       1000          HPPC_LongHashSet  avgt    3     33.620 ±       2.425  ns/op
LongHashSetsBench.search       1000       HPPC_RT_LongHashSet  avgt    3     34.134 ±       3.058  ns/op
LongHashSetsBench.search       1000  FastUtil_LongOpenHashSet  avgt    3     31.307 ±       1.978  ns/op
LongHashSetsBench.search       1000     Koloboke_HashLongSets  avgt    3     32.899 ±       1.874  ns/op
LongHashSetsBench.search       1000       Eclipse_LongHashSet  avgt    3     40.472 ±       2.099  ns/op
LongHashSetsBench.search       1000        Agrona_LongHashSet  avgt    3     31.633 ±       1.114  ns/op
LongHashSetsBench.search       2000        MutableLongHashSet  avgt    3     35.562 ±       2.001  ns/op
LongHashSetsBench.search       2000              TLongHashSet  avgt    3     47.423 ±       4.964  ns/op
LongHashSetsBench.search       2000          HPPC_LongHashSet  avgt    3     34.461 ±       0.962  ns/op
LongHashSetsBench.search       2000       HPPC_RT_LongHashSet  avgt    3     34.776 ±       2.519  ns/op
LongHashSetsBench.search       2000  FastUtil_LongOpenHashSet  avgt    3     31.516 ±       4.588  ns/op
LongHashSetsBench.search       2000     Koloboke_HashLongSets  avgt    3     33.205 ±       2.412  ns/op
LongHashSetsBench.search       2000       Eclipse_LongHashSet  avgt    3     42.154 ±       2.609  ns/op
LongHashSetsBench.search       2000        Agrona_LongHashSet  avgt    3     31.984 ±       3.164  ns/op
LongHashSetsBench.search       5000        MutableLongHashSet  avgt    3     41.138 ±       0.484  ns/op
LongHashSetsBench.search       5000              TLongHashSet  avgt    3     48.515 ±       4.468  ns/op
LongHashSetsBench.search       5000          HPPC_LongHashSet  avgt    3     40.477 ±       2.599  ns/op
LongHashSetsBench.search       5000       HPPC_RT_LongHashSet  avgt    3     40.965 ±       2.186  ns/op
LongHashSetsBench.search       5000  FastUtil_LongOpenHashSet  avgt    3     37.078 ±       2.052  ns/op
LongHashSetsBench.search       5000     Koloboke_HashLongSets  avgt    3     37.977 ±       1.169  ns/op
LongHashSetsBench.search       5000       Eclipse_LongHashSet  avgt    3     35.476 ±       2.710  ns/op
LongHashSetsBench.search       5000        Agrona_LongHashSet  avgt    3     24.092 ±       1.137  ns/op
LongHashSetsBench.search      10000        MutableLongHashSet  avgt    3     41.355 ±       3.509  ns/op
LongHashSetsBench.search      10000              TLongHashSet  avgt    3     50.923 ±       4.298  ns/op
LongHashSetsBench.search      10000          HPPC_LongHashSet  avgt    3     41.333 ±       2.074  ns/op
LongHashSetsBench.search      10000       HPPC_RT_LongHashSet  avgt    3     42.084 ±       1.285  ns/op
LongHashSetsBench.search      10000  FastUtil_LongOpenHashSet  avgt    3     37.911 ±       1.537  ns/op
LongHashSetsBench.search      10000     Koloboke_HashLongSets  avgt    3     39.188 ±       2.662  ns/op
LongHashSetsBench.search      10000       Eclipse_LongHashSet  avgt    3     36.482 ±       0.829  ns/op
LongHashSetsBench.search      10000        Agrona_LongHashSet  avgt    3     25.333 ±       0.900  ns/op
LongHashSetsBench.search      20000        MutableLongHashSet  avgt    3     42.919 ±       1.103  ns/op
LongHashSetsBench.search      20000              TLongHashSet  avgt    3     50.844 ±       3.559  ns/op
LongHashSetsBench.search      20000          HPPC_LongHashSet  avgt    3     45.406 ±       3.098  ns/op
LongHashSetsBench.search      20000       HPPC_RT_LongHashSet  avgt    3     44.063 ±       2.585  ns/op
LongHashSetsBench.search      20000  FastUtil_LongOpenHashSet  avgt    3     39.875 ±       0.527  ns/op
LongHashSetsBench.search      20000     Koloboke_HashLongSets  avgt    3     41.993 ±      15.756  ns/op
LongHashSetsBench.search      20000       Eclipse_LongHashSet  avgt    3     48.495 ±      30.222  ns/op
LongHashSetsBench.search      20000        Agrona_LongHashSet  avgt    3     27.246 ±       0.702  ns/op
LongHashSetsBench.search      50000        MutableLongHashSet  avgt    3     42.609 ±       2.318  ns/op
LongHashSetsBench.search      50000              TLongHashSet  avgt    3     53.472 ±       4.268  ns/op
LongHashSetsBench.search      50000          HPPC_LongHashSet  avgt    3     34.787 ±       0.966  ns/op
LongHashSetsBench.search      50000       HPPC_RT_LongHashSet  avgt    3     35.913 ±       1.990  ns/op
LongHashSetsBench.search      50000  FastUtil_LongOpenHashSet  avgt    3     32.633 ±       1.819  ns/op
LongHashSetsBench.search      50000     Koloboke_HashLongSets  avgt    3     35.255 ±       2.310  ns/op
LongHashSetsBench.search      50000       Eclipse_LongHashSet  avgt    3     46.110 ±       2.575  ns/op
LongHashSetsBench.search      50000        Agrona_LongHashSet  avgt    3     33.692 ±       2.518  ns/op
LongHashSetsBench.search     100000        MutableLongHashSet  avgt    3     43.100 ±       4.643  ns/op
LongHashSetsBench.search     100000              TLongHashSet  avgt    3     60.145 ±       4.324  ns/op
LongHashSetsBench.search     100000          HPPC_LongHashSet  avgt    3     41.764 ±       3.327  ns/op
LongHashSetsBench.search     100000       HPPC_RT_LongHashSet  avgt    3     42.451 ±       3.575  ns/op
LongHashSetsBench.search     100000  FastUtil_LongOpenHashSet  avgt    3     39.060 ±       2.277  ns/op
LongHashSetsBench.search     100000     Koloboke_HashLongSets  avgt    3     41.505 ±       3.425  ns/op
LongHashSetsBench.search     100000       Eclipse_LongHashSet  avgt    3     53.318 ±      37.256  ns/op
LongHashSetsBench.search     100000        Agrona_LongHashSet  avgt    3     40.143 ±       2.731  ns/op

Deep sizes by JOL:

MutableLongHashSet	20	552
TLongHashSet	20	488
HPPC_LongHashSet	20	368
HPPC_RT_LongHashSet	20	736
FastUtil_LongOpenHashSet	20	328
Koloboke_HashLongSets	20	496
Eclipse_LongHashSet	20	568
Agrona_LongHashSet	20	568

MutableLongHashSet	50	552
TLongHashSet	50	1336
HPPC_LongHashSet	50	1136
HPPC_RT_LongHashSet	50	1504
FastUtil_LongOpenHashSet	50	1096
Koloboke_HashLongSets	50	1264
Eclipse_LongHashSet	50	1080
Agrona_LongHashSet	50	1080

MutableLongHashSet	100	2088
TLongHashSet	100	2592
HPPC_LongHashSet	100	2160
HPPC_RT_LongHashSet	100	2528
FastUtil_LongOpenHashSet	100	2120
Koloboke_HashLongSets	100	2288
Eclipse_LongHashSet	100	2104
Agrona_LongHashSet	100	2104

MutableLongHashSet	200	4136
TLongHashSet	200	4000
HPPC_LongHashSet	200	4208
HPPC_RT_LongHashSet	200	4576
FastUtil_LongOpenHashSet	200	4168
Koloboke_HashLongSets	200	4336
Eclipse_LongHashSet	200	4152
Agrona_LongHashSet	200	4152

MutableLongHashSet	500	8232
TLongHashSet	500	9448
HPPC_LongHashSet	500	8304
HPPC_RT_LongHashSet	500	8672
FastUtil_LongOpenHashSet	500	8264
Koloboke_HashLongSets	500	8432
Eclipse_LongHashSet	500	8248
Agrona_LongHashSet	500	8248

MutableLongHashSet	1000	16424
TLongHashSet	1000	18832
HPPC_LongHashSet	1000	16496
HPPC_RT_LongHashSet	1000	16864
FastUtil_LongOpenHashSet	1000	16456
Koloboke_HashLongSets	1000	16624
Eclipse_LongHashSet	1000	16440
Agrona_LongHashSet	1000	16440

MutableLongHashSet	2000	32808
TLongHashSet	2000	37696
HPPC_LongHashSet	2000	32880
HPPC_RT_LongHashSet	2000	33248
FastUtil_LongOpenHashSet	2000	32840
Koloboke_HashLongSets	2000	33008
Eclipse_LongHashSet	2000	32824
Agrona_LongHashSet	2000	32824

MutableLongHashSet	5000	65576
TLongHashSet	5000	92288
HPPC_LongHashSet	5000	65648
HPPC_RT_LongHashSet	5000	66016
FastUtil_LongOpenHashSet	5000	65608
Koloboke_HashLongSets	5000	65776
Eclipse_LongHashSet	5000	131128
Agrona_LongHashSet	5000	131128

MutableLongHashSet	10000	131112
TLongHashSet	10000	184664
HPPC_LongHashSet	10000	131184
HPPC_RT_LongHashSet	10000	131552
FastUtil_LongOpenHashSet	10000	131144
Koloboke_HashLongSets	10000	131312
Eclipse_LongHashSet	10000	262200
Agrona_LongHashSet	10000	262200

MutableLongHashSet	20000	262184
TLongHashSet	20000	369256
HPPC_LongHashSet	20000	262256
HPPC_RT_LongHashSet	20000	262624
FastUtil_LongOpenHashSet	20000	262216
Koloboke_HashLongSets	20000	262384
Eclipse_LongHashSet	20000	524344
Agrona_LongHashSet	20000	524344

MutableLongHashSet	50000	524328
TLongHashSet	50000	925992
HPPC_LongHashSet	50000	1048688
HPPC_RT_LongHashSet	50000	1049056
FastUtil_LongOpenHashSet	50000	1048648
Koloboke_HashLongSets	50000	1048816
Eclipse_LongHashSet	50000	1048632
Agrona_LongHashSet	50000	1048632

MutableLongHashSet	100000	1048616
TLongHashSet	100000	1851928
HPPC_LongHashSet	100000	2097264
HPPC_RT_LongHashSet	100000	2097632
FastUtil_LongOpenHashSet	100000	2097224
Koloboke_HashLongSets	100000	2097392
Eclipse_LongHashSet	100000	2097208
Agrona_LongHashSet	100000	2097208
 */
