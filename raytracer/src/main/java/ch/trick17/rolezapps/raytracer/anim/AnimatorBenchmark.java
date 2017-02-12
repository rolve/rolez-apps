package ch.trick17.rolezapps.raytracer.anim;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;
import static rolez.lang.Task.currentTask;

import java.io.IOException;

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

import ch.trick17.rolezapps.raytracer.Raytracer;
import ch.trick17.rolezapps.raytracer.util.VideoWriterJava;
import rolez.lang.Task;
import rolez.util.Random;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class AnimatorBenchmark {
    
    private static final String MOVIE_FILE = "movie.mp4";

    @Param({"90"})
    int height;
    
    @Param({""})
    String impl;
    
    @Param({"1", "8"})
    int tasks;
    
    Animator animator;
    
    @Setup(Level.Iteration)
    public void setup() throws IOException {
        Task.registerNewRootTask();
        AnimatedScene scene = new AnimatedScene(3.0, currentTask());
        AnimatorApp.INSTANCE.buildScene(scene, new Random(42), currentTask());
        int width = (int) (height * scene.view.aspect);
        
        Raytracer raytracer = instantiateBenchmark(Raytracer.class, impl, currentTask());
        raytracer.numTasks = tasks;
        raytracer.maxRecursions = 3;
        
        VideoWriterJava writer = new VideoWriterJava(MOVIE_FILE, width, height, 25, 12);
        
        animator = instantiateBenchmark(Animator.class, impl, raytracer, scene, writer);
    }
    
    @Benchmark
    public void animator() {
        animator.render(currentTask());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(AnimatorBenchmark.class.getSimpleName())
                .warmupIterations(5).measurementIterations(30).build();
        new Runner(options).run();
    }
}
