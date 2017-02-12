package ch.trick17.rolezapps.montecarlo;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static java.lang.Math.abs;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;
import static rolez.lang.Task.currentTask;

import java.util.HashMap;
import java.util.Map;

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
public class MonteCarloBenchmark {
    
    private static final String FILE = MonteCarloAppRunner.INSTANCE.file;
    private static final int TIME_STEPS = MonteCarloAppRunner.INSTANCE.steps;
    private static final Map<Integer, Double> REF_VALS = new HashMap<Integer, Double>() {{
        put(10000, -0.0333976656762814);
        put(60000, -0.03215796752868655);
    }};
    
    @Param({"10000", /* "60000" */})
    int runs;
    
    @Param({"1", "2", "4", "8", "32", "64"})
    int tasks;
    
    @Param({"", "Java"})
    String impl;
    
    MonteCarloApp app;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        app = instantiateBenchmark(MonteCarloApp.class, impl, FILE, TIME_STEPS, runs, tasks,
                currentTask());
    }
    
    @Benchmark
    public void monteCarlo() {
        app.run(currentTask());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        double expectedReturnRate = app.avgExpectedReturnRate(currentTask());
        double dev = abs(expectedReturnRate - REF_VALS.get(runs));
        if(dev > 1.0e-12)
            throw new AssertionError("Validation failed");
        
        Task.unregisterRootTask();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(MonteCarloBenchmark.class.getSimpleName())
                .warmupIterations(5).measurementIterations(5).build();
        new Runner(options).run();
    }
}
