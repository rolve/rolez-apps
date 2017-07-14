package ch.trick17.rolezapps.nbody;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static ch.trick17.rolezapps.BenchmarkUtils.runAndPlot;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;

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

import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class NBodyBenchmark {
    
    @Param({"500", "1000", "2000"})
    int n;
    
    int iterations = 30;
    
    @Param({""})
    String impl;
    
    @Param({"1", "2", "4"})
    int tasks;
    
    NBody nbody;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        nbody = instantiateBenchmark(NBody.class, impl, n, iterations, tasks, Task.currentTask().idBits());
        nbody.createSystem(new Random(42), Task.currentTask().idBits());
    }
    
    @Benchmark
    public void idea() {
        nbody.simulate(Task.currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(NBodyBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        runAndPlot(options);
    }
}
