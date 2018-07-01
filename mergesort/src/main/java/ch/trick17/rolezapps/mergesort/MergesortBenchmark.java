package ch.trick17.rolezapps.mergesort;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static ch.trick17.rolezapps.BenchmarkUtils.intValueForParam;
import static ch.trick17.rolezapps.BenchmarkUtils.runAndStoreResults;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;
import static rolez.lang.Task.currentTask;

import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import ch.trick17.rolezapps.IntValues;
import rolez.lang.GuardedArray;
import rolez.lang.MathExtra;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class MergesortBenchmark {

    @Param({"small", "medium", "large"})
    @IntValues({300000, 1500000, 6000000})
    String size;
    
    @Param({"RolezEager", "Rolez", "Java"})
    String impl;
    
    @Param({"1", "2", "4", "8", "16", "32"})
    int tasks;
    
    Mergesort mergesort;
    GuardedArray<int[]> data1;
    GuardedArray<int[]> data2;
    GuardedArray<int[]> data3;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        Random random = new Random(42);
        int maxLevel = MathExtra.INSTANCE.log2(tasks, currentTask().idBits());
        mergesort = instantiateBenchmark(Mergesort.class, impl, maxLevel, currentTask().idBits());
        int n = intValueForParam(this, "size");
        data1 = mergesort.shuffledInts$Unguarded(n, random, currentTask().idBits());
        data2 = mergesort.shuffledInts$Unguarded(n, random, currentTask().idBits());
        data3 = mergesort.shuffledInts$Unguarded(n, random, currentTask().idBits());
    }
    
    @Benchmark
    public void mergesort() {
        mergesort.sort$Unguarded(data1, currentTask().idBits());
        mergesort.sort$Unguarded(data2, currentTask().idBits());
        mergesort.sort$Unguarded(data3, currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(MergesortBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        runAndStoreResults(options);
    }
}
