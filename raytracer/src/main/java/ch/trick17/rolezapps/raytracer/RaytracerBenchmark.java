package ch.trick17.rolezapps.raytracer;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class RaytracerBenchmark {
    
    @Param({"180", "360"})
    int height;
    
    @Param({"1", "2", "4", "8", "32", "128"})
    int tasks;
    
    @Param({"Rolez", "Java"})
    String impl;
    
    RaytracerBenchmarkSetup setup;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        Random random = new Random(42);
        setup = instantiateBenchmark(RaytracerBenchmarkSetup.class, impl, height, tasks, random);
    }
    
    @Benchmark
    public int raytracer() {
        return setup.runRaytracer();
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(RaytracerBenchmark.class.getSimpleName())
                .warmupIterations(20).measurementIterations(30).build();
        new Runner(options).run();
    }
}
