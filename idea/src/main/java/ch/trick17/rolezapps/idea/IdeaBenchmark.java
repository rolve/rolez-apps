package ch.trick17.rolezapps.idea;

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

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class IdeaBenchmark {
    
    @Param({"3000000", "20000000", "50000000"})
    int n;
    
    @Param({""})
    String impl;
    
    @Param({"1", "2", "4", "16", "64"})
    int tasks;
    
    IdeaEncryption idea;
    
    @Setup(Level.Iteration)
    public void setup() {
        idea = instantiateBenchmark(IdeaEncryption.class, impl, n, tasks);
        idea.buildTestData(new Random(136506717L));
    }
    
    @Benchmark
    public void idea() {
        idea.run();
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        idea.validate();
    }
    
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(IdeaBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        new Runner(options).run();
    }
}
