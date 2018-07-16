package ch.trick17.rolezapps.raytracer.anim;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static ch.trick17.rolezapps.BenchmarkUtils.intValueForParam;
import static ch.trick17.rolezapps.BenchmarkUtils.runAndPlot;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;
import static rolez.lang.Task.currentTask;

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
public class AnimatorBenchmark {

    @Param({"small", "medium", "large"})
    @IntValues({45, 90, 180})
    String size;
    
    @Param({"RolezEager", "Rolez", "Java"})
    String impl;
    
    @Param({"1", "2", "4", "8", "16", "32"})
    int tasks;
    
    AnimatorBenchmarkSetup setup;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        setup = instantiateBenchmark(AnimatorBenchmarkSetup.class, impl,
                intValueForParam(this, "size"), tasks, currentTask().idBits());
    }
    
    @Benchmark
    public void animator() {
        setup.runAnimator$Unguarded(currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(AnimatorBenchmark.class.getSimpleName())
                .warmupIterations(5).measurementIterations(30).build();
        runAndPlot(options);
    }
}
