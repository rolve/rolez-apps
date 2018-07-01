package ch.trick17.rolezapps.histogram;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static ch.trick17.rolezapps.BenchmarkUtils.intValueForParam;
import static ch.trick17.rolezapps.BenchmarkUtils.runAndStoreResults;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;
import static rolez.lang.Task.currentTask;

import java.io.IOException;

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
import ch.trick17.rolezapps.histogram.util.ImageReaderJava;
import rolez.lang.GuardedArray;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class HistogramBenchmark {
    
    @Param({"small", "medium", "large"})
    @IntValues({12000000, 50000000, 100000000})
    String size;
    
    @Param({"Rolez", "Java"})
    String impl;
    
    @Param({"1", "2", "4", "8", "16", "32"})
    int tasks;
    
    GuardedArray<GuardedArray<int[]>[]> image;
    Histogram histogram;
    
    @Setup(Level.Trial)
    public void readImage() throws IOException {
        Task.registerNewRootTask();
        image = ImageReaderJava.read(intValueForParam(this, "size") + ".jpg");
    }
    
    @Setup(Level.Iteration)
    public void setup() {
        histogram = instantiateBenchmark(Histogram.class, impl, image, currentTask().idBits());
    }
    
    @Benchmark
    public void histogram() {
        histogram.compute$Unguarded(tasks, currentTask().idBits());
    }
    
    @TearDown(Level.Trial)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(HistogramBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        runAndStoreResults(options);
    }
}
