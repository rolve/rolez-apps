package ch.trick17.rolezapps.histogram;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static ch.trick17.rolezapps.BenchmarkUtils.runAndPlot;
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

import ch.trick17.rolezapps.histogram.util.ImageReaderJava;
import rolez.lang.GuardedArray;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class HistogramBenchmark {
    
    @Param({"6000000", "24000000", "50000000"})
    int n;
    
    @Param({"Rolez", "RolezImmutable", "Java"})
    String impl;
    
    @Param({"1", "2", "4", "8", "16"})
    int tasks;
    
    GuardedArray<GuardedArray<int[]>[]> image;
    Histogram histogram;
    
    @Setup(Level.Trial)
    public void readImage() throws IOException {
        image = ImageReaderJava.read(n + ".jpg");
    }
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        histogram = instantiateBenchmark(Histogram.class, impl, image, currentTask().idBits());
    }
    
    @Benchmark
    public void histogram() {
        histogram.compute(tasks, currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(HistogramBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        runAndPlot(options);
    }
}
