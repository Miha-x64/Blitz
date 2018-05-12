package net.aquadc.blitz;

import com.carrotsearch.hppc.LongHashSet;
import com.koloboke.collect.set.hash.HashLongSet;
import gnu.trove.set.hash.TLongHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class LongHashSetsBench {

    @Param({
            "MutableLongHashSet",
            /*"TreeSet", "HashSet",*/
            "TLongHashSet", "HPPC_LongHashSet",
            "HPPC_RT_LongHashSet", "FastUtil_LongOpenHashSet",
            "Koloboke_HashLongSets",
            "Eclipse_LongHashSet",
            "Agrona_LongHashSet"
    })
    LongSetType setType;

    @Param({
            "20", "50", "100",
            "200", "500", "1000",
            "2000", "5000", "10000",
            "20"+"000", "50"+"000", "100"+"000"
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

}
