package net.aquadc.blitz;

import net.aquadc.blitz.impl.MutableLongHashSet;
import net.aquadc.blitz.impl.MutableLongTreeSet;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

@State(Scope.Benchmark)
public class PerformanceBenchmark {

    private final Random random = new Random();

    @Param({ "MutableLongTreeSet", "MutableLongHashSet", "TreeSet", "HashSet" })
    String setClass;

    @Param({ "10", "100", "1000", "10" + "000", "100" + "000" })
    int setSize;

    private MutableLongSet pSet;
    private Set<Long> jSet;

    @Setup
    public void before() {
        switch (setClass) {
            case "MutableLongTreeSet":
                pSet = new MutableLongTreeSet(setSize);
                break;

            case "MutableLongHashSet":
                pSet = new MutableLongHashSet(setSize);
                break;

            case "TreeSet":
                jSet = new TreeSet<>();
                break;

            case "HashSet":
                jSet = new HashSet<>(setSize);
                break;

            default:
                throw new AssertionError();
        }

        if (pSet != null) {
            for (int i = 0; i < setSize; i++) {
                pSet.add(random.nextLong());
            }
        } else if (jSet != null) {
            for (int i = 0; i < setSize; i++) {
                jSet.add(random.nextLong());
            }
        } else {
            throw new AssertionError();
        }
    }

    @Benchmark
    public void insert() {
        if (pSet != null) {
            pSet.add(random.nextLong());
        } else if (jSet != null) {
            jSet.add(random.nextLong());
        } else {
            throw new AssertionError();
        }
    }

    @Benchmark
    public void search(Blackhole bh) {
        if (pSet != null) {
            bh.consume(pSet.contains(random.nextLong()));
        } else if (jSet != null) {
            bh.consume(jSet.contains(random.nextLong()));
        } else {
            throw new AssertionError();
        }
    }

}
