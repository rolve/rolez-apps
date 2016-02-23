package ch.trick17.rolezapps.quicksort;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import rolez.lang.GuardedArray;
import rolez.lang.TaskSystem;

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@State(Scope.Thread)
public class QuicksortBenchmark {
    
    @Param({"100", "1000", "10000", "100000"})
    int n;
    
    Quicksort quicksort;
    GuardedArray<int[]> data;
    
    @Setup(Level.Invocation)
    public void setup() {
        quicksort = new Quicksort();
        data = quicksort.shuffledInts(n);
    }
    
    @Benchmark
    public void quicksort() {
        TaskSystem.getDefault().run(quicksort.$sortTask(data));
    }
    
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(QuicksortBenchmark.class.getSimpleName())
                .jvmArgs("-server").build();
        new Runner(options).run();
    }
}
