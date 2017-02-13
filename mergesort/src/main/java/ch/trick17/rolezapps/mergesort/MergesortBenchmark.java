package ch.trick17.rolezapps.mergesort;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;
import static rolez.lang.Task.currentTask;

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
public class MergesortBenchmark {
    
    @Param({"2000000"})
    int n;
    
    @Param({"", "Java"})
    String impl;
    
    @Param({"1", "2", "4", "16", "64"})
    int tasks;
    
    Mergesort mergesort;
    GuardedArray<int[]> data;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        int maxLevel = MathExtra.INSTANCE.log2(tasks, currentTask().idBits());
        mergesort = instantiateBenchmark(Mergesort.class, impl, maxLevel, currentTask().idBits());
        data = mergesort.shuffledInts(n, currentTask().idBits());
    }
    
    @Benchmark
    public void quicksort() {
        mergesort.sort(data, currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(MergesortBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        new Runner(options).run();
    }
}
