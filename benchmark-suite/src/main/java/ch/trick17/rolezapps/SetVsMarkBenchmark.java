package ch.trick17.rolezapps;

import static java.util.Collections.newSetFromMap;

import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 20)
@Measurement(iterations = 20)
@Fork(1)
public class SetVsMarkBenchmark {

    private static final int N = 100000;

    private Markable[] objects;

    private Set<Object> set;

    @Setup(Level.Iteration)
    public void setup() {
        objects = new Markable[N];
        for (int i = 0; i < N; i++) {
            objects[i] = new Markable();
        }

        set = newSetFromMap(new IdentityHashMap<Object, Boolean>());
    }

    @Benchmark
    public void mark() {
        for (int i = 0; i < N; i++) {
            objects[i].bits |= 0x0001;
        }
    }

    @Benchmark
    public void addToSet() {
        for (int i = 0; i < N; i++) {
            set.add(objects[i]);
        }
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder().include(SetVsMarkBenchmark.class.getSimpleName()).build()).run();
    }

    static class Markable {
        volatile long bits;
    }
}
