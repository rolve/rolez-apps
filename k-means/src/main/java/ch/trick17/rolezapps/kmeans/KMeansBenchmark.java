package ch.trick17.rolezapps.kmeans;

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
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class KMeansBenchmark {
    
    private static final int maxIters = 50;
    
    int dim = 10;
    
    @Param({"small", "medium", "large"})
    @IntValues({20000, 40000, 60000})
    String size;
    
    @Param({"Rolez", "Java"})
    String impl;
    
    int clusters;
    
    @Param({"1", "2", "4", "8", "16", "32"})
    int tasks;
    
    KMeans kMeans;
    GuardedArray<double[][]> data;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        int n = intValueForParam(this, "size");
        clusters = n  / 100;
        kMeans = instantiateBenchmark(KMeans.class, impl, dim, clusters, tasks,
                currentTask().idBits());
        data = kMeans.createDataSet$Unguarded(n, new Random(42), currentTask().idBits());
    }
    
    @Benchmark
    public Object kMeans() {
        return kMeans.kMeans$Unguarded(data, maxIters, currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(KMeansBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        runAndStoreResults(options);
    }
}
