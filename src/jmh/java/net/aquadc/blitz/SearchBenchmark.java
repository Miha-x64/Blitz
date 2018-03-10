package net.aquadc.blitz;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class SearchBenchmark {

    private final Random srchRandom = new Random("search".hashCode());

    @Param({ "MutableLongTreeSet", "MutableLongHashSet", "TreeSet", "HashSet" })
    String setClass;

    @Param({
            "20", "50", "100",
            "200", "500", "1000",
            "2000", "5000", "10000",
            "20"+"000", "50"+"000", "100"+"000",
    })
    int setSize;

    private MutableLongSet pSet;
    private Set<Long> jSet;

    @Setup
    public void before() {
        Object set = Common.createAndFill(setClass, setSize);
        if (set instanceof MutableLongSet) {
            pSet = (MutableLongSet) set;
        } else if (set instanceof Set<?>) {
            jSet = (Set<Long>) set;
        } else {
            throw new AssertionError();
        }
    }

    @Benchmark
    public void search(Blackhole bh) {
        if (pSet != null) {
            bh.consume(pSet.contains(srchRandom.nextLong()));
        } else if (jSet != null) {
            bh.consume(jSet.contains(srchRandom.nextLong()));
        } else {
            throw new AssertionError();
        }
    }

    public static void main(String[] args) throws Exception {
        new Runner(
                new OptionsBuilder()
                        .include(SearchBenchmark.class.getSimpleName())
                        .mode(Mode.AverageTime)
                        .forks(1)
                        .timeUnit(TimeUnit.NANOSECONDS)
                        .warmupIterations(3)
                        .measurementIterations(3)
                        .build()
        ).run();
    }

}
