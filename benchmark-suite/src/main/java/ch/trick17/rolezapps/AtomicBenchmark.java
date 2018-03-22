package ch.trick17.rolezapps;

import static java.util.concurrent.atomic.AtomicIntegerFieldUpdater.newUpdater;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
public class AtomicBenchmark {

    A a;
    A[] as;

    @Setup
    public void setup() {
        a = new A();
        
        as = new A[100000];
        for(int i = 0; i < as.length; i++) {
            as[i] = new A();
        }
    }

//    @Benchmark
//    public void overhead() {}
//
//    @Benchmark
//    public int plainRead() {
//        return a.i;
//    }
//
//    @Benchmark
//    public void plainWrite() {
//        a.i = 0;
//    }
//
//    @Benchmark
//    public int updaterGet() {
//        return A.updater.get(a);
//    }
//
//    @Benchmark
//    public void updaterSet() {
//        A.updater.set(a, 0);
//    }
//
//    @Benchmark
//    public void updaterCAS() {
//        A.updater.compareAndSet(a, 0, 1);
//    }
//
//    @Benchmark
//    public int atomicGet() {
//        return a.ai.get();
//    }
//
//    @Benchmark
//    public void atomicSet() {
//        a.ai.set(0);
//    }
//
//    @Benchmark
//    public void atomicCAS() {
//        a.ai.compareAndSet(0, 1);
//    }

    @Benchmark
    public void manyPlainRead(Blackhole hole) {
        for(int i = 0; i < as.length; i++) {
            hole.consume(as[i].i);
        }
    }

    @Benchmark
    public void manyAtomicGet(Blackhole hole) {
        for(int i = 0; i < as.length; i++) {
            hole.consume(as[i].ai.get());
        }
    }

    @Benchmark
    public void manyUpdaterCAS() {
        for(int i = 0; i < as.length; i++) {
            A.updater.compareAndSet(as[i], 0, 1);
        }
    }

    @Benchmark
    public void manyAtomicCAS() {
        for(int i = 0; i < as.length; i++) {
            as[i].ai.compareAndSet(0, 1);
        }
    }

    @Benchmark
    public void manySynchronized() {
        for(int i = 0; i < as.length; i++) {
            synchronized(as[i]) {
                as[i].i = 1;
            }
        }
    }

    static class A {
        static final AtomicIntegerFieldUpdater<A> updater = newUpdater(A.class, "i");
        volatile int i;
        final AtomicInteger ai = new AtomicInteger();
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder().include(AtomicBenchmark.class.getSimpleName()).build()).run();
    }
}
