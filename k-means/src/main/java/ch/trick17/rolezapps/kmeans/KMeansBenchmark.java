package ch.trick17.rolezapps.kmeans;

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
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class KMeansBenchmark {
    
    int dim = 10;
    
    @Param({"10000"})
    int n;
    
    @Param({"50"})
    int maxIters;
    
    @Param({"", "LocalOpt", "Java"})
    String impl;
    
    int clusters;
    
    @Param({"1", "2", "4", "8"})
    int tasks;
    
    KMeans kMeans;
    GuardedArray<double[][]> data;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.resetTaskIdCounter();
        Task.registerNewRootTask();
        clusters = n / 100;
        kMeans = instantiateBenchmark(KMeans.class, impl, dim, clusters, tasks);
        data = kMeans.createDataSet(n);
    }
    
    @Benchmark
    public Object kMeans() {
        return kMeans.kMeans(data, maxIters);
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(KMeansBenchmark.class.getSimpleName())
                .warmupIterations(20).measurementIterations(30).build();
        new Runner(options).run();
    }
}
