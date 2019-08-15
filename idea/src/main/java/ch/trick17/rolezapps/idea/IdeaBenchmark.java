package ch.trick17.rolezapps.idea;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static ch.trick17.rolezapps.BenchmarkUtils.intValueForParam;
import static ch.trick17.rolezapps.BenchmarkUtils.runAndStoreResults;
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

import ch.trick17.rolezapps.IntValues;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class IdeaBenchmark {
    
    @Param({"small"})
    @IntValues({3000000, 20000000, 50000000})
    String size;
    
    @Param({"Rolez", "RolezOpt", "Java"})
    String impl;
    
    @Param({"1", "2", "4", "8", "16", "32"})
    int tasks;
    
    IdeaEncryption idea;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        idea = instantiateBenchmark(IdeaEncryption.class, impl,
                intValueForParam(this, "size"), tasks, Task.currentTask().idBits());
        idea.buildTestData$Unguarded(new Random(136506717L), Task.currentTask().idBits());
    }
    
    @Benchmark
    public void idea() {
        idea.run$Unguarded(Task.currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        idea.validate$Unguarded(Task.currentTask().idBits());
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(IdeaBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        runAndStoreResults(options);
    }
}
