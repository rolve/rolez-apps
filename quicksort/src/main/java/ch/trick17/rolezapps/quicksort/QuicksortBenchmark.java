package ch.trick17.rolezapps.quicksort;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static ch.trick17.rolezapps.BenchmarkUtils.runAndPlot;
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

import rolez.lang.GuardedArray;
import rolez.lang.MathExtra;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(value = 1, jvmArgsAppend = {"-XX:+UnlockExperimentalVMOptions", "-XX:+TrustFinalNonStaticFields"})
@State(Thread)
public class QuicksortBenchmark {
    
    @Param({"300000", "1500000", "6000000"})
    int n;
    
    @Param({"Rolez", "RolezL", "Java"})
    String impl;
    
    @Param({"1", "2", "4", "8", "16", "32"})
    int tasks;
    
    Quicksort quicksort;
    GuardedArray<int[]> data1;
    GuardedArray<int[]> data2;
    GuardedArray<int[]> data3;
    
    @Setup(Level.Iteration)
    public void setup() {
        Random random = new Random(42);
        Task.registerNewRootTask();
        int maxLevel = MathExtra.INSTANCE.log2(tasks, currentTask().idBits());
        quicksort = instantiateBenchmark(Quicksort.class, impl, maxLevel, currentTask().idBits());
        data1 = quicksort.shuffledInts(n, random, currentTask().idBits());
        data2 = quicksort.shuffledInts(n, random, currentTask().idBits());
        data3 = quicksort.shuffledInts(n, random, currentTask().idBits());
    }
    
    @Benchmark
    public void quicksort() {
        quicksort.sort(data1, currentTask().idBits());
        quicksort.sort(data2, currentTask().idBits());
        quicksort.sort(data3, currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(QuicksortBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        runAndPlot(options);
    }
}
