package ch.trick17.rolezapps.quicksort;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import rolez.lang.GuardedArray;
import rolez.lang.MathExtra;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class QuicksortBenchmark {
    
    @Param({"2000000"})
    int n;
    
    @Param({"", "LocalOpt", "Java"})
    String impl;
    
    @Param({"1", "2", "4", "16", "64"})
    int tasks;
    
    Quicksort quicksort;
    GuardedArray<int[]> data;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        int maxLevel = MathExtra.INSTANCE.log2(tasks);
        quicksort = instantiateBenchmark(Quicksort.class, impl, maxLevel);
        data = quicksort.shuffledInts(n);
    }
    
    @Benchmark
    public void quicksort() {
        quicksort.sort(data);
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(QuicksortBenchmark.class.getSimpleName())
                .warmupIterations(20).measurementIterations(30).build();
        new Runner(options).run();
    }
}
