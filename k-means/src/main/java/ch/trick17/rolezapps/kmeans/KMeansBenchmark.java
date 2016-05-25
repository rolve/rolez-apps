package ch.trick17.rolezapps.kmeans;

import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;

import java.util.concurrent.Callable;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import rolez.lang.GuardedArray;
import rolez.lang.TaskSystem;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class KMeansBenchmark {
    
    int dim = 10;
    
    @Param({"1000"})
    int n;
    
    int clusters;
    
    @Param({"1", "2", "4", "8", "16", "32", "64", "128", "256"})
    int tasks;
    
    KMeans kMeans;
    GuardedArray<GuardedArray<double[]>[]> data;
    
    @Setup(Level.Iteration)
    public void setup() {
        clusters = n / 100;
        kMeans = new KMeans(dim, clusters, tasks);
        data = kMeans.createDataSet(n);
    }
    
    @Benchmark
    public void kMeans() {
        TaskSystem.getDefault().run(new Callable<Void>() {
            public Void call() {
                kMeans.kMeans(data);
                return null;
            }
        });
    }
    
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(KMeansBenchmark.class.getSimpleName())
                .warmupIterations(20).measurementIterations(30).build();
        new Runner(options).run();
    }
}