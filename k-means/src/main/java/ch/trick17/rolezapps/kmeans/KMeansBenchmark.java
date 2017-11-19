package ch.trick17.rolezapps.kmeans;

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

import classes.AppKmeans;
import rolez.checked.lang.CheckedArray;
import rolez.checked.lang.Vector;
import rolez.lang.GuardedArray;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class KMeansBenchmark {
    
    private static final int maxIters = 50;
    
    int dim = 10;
    
    @Param({"20000"})
    int n;
    
    @Param({"Checked", "Rolez", "Java"})
    String impl;
    
    int clusters;
    
    @Param({"1"})
    int tasks;
    
    KMeans kMeans;
    AppKmeans kMeansC;
    GuardedArray<double[][]> data;
    CheckedArray<double[][]> cData;
    
    @Setup(Level.Iteration)
    public void setup() {
        clusters = n / 100;
    	if (impl.equals("Checked")) {
    		rolez.checked.lang.Task.registerNewRootTask();
    		kMeansC = new AppKmeans(dim, clusters, tasks, rolez.checked.lang.Task.currentTask().idBits());
    		cData = kMeansC.createDataSet(n, new Random(42), rolez.checked.lang.Task.currentTask().idBits());
    		return;
    	}
        Task.registerNewRootTask();
        kMeans = instantiateBenchmark(KMeans.class, impl, dim, clusters, tasks,
                currentTask().idBits());
        data = kMeans.createDataSet(n, new Random(42), currentTask().idBits());
    }
    
    @Benchmark
    public Object kMeans() {
    	if (impl.equals("Checked")) {
    		return kMeansC.kMeans(cData, maxIters, rolez.checked.lang.Task.currentTask().idBits());
    	}
        return kMeans.kMeans(data, maxIters, currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
    	if (impl.equals("Checked"))
    		rolez.checked.lang.Task.unregisterRootTask();
    	else
    		Task.unregisterRootTask();
    }
    
    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(KMeansBenchmark.class.getSimpleName())
                .warmupIterations(0).measurementIterations(5).build();
        runAndPlot(options);
    }
}
