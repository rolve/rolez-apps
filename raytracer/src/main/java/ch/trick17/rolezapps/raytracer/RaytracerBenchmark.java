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

import ch.trick17.rolezapps.raytracer.anim.AnimatedScene;
import ch.trick17.rolezapps.raytracer.anim.AnimatorApp;
import rolez.lang.Guarded;
import rolez.lang.GuardedArray;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class RaytracerBenchmark {
    
    @Param({"180", "360"})
    int height;
    
    @Param({""})
    String impl;
    
    @Param({"1", "2", "4", "8", "32", "128"})
    int tasks;
    
    Raytracer raytracer;
    GuardedArray<GuardedArray<int[]>[]> image;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        AnimatedScene scene = createBenchmarkScene();
        
        int width = (int) (height * scene.view.aspect);
        image = GuardedArray.wrap(new int[height][width]);
        
        raytracer = instantiateBenchmark(Raytracer.class, impl);
        raytracer.numTasks = tasks;
        raytracer.maxRecursions = 5;
        raytracer.scene = scene;
    }

    private static AnimatedScene createBenchmarkScene() {
        AnimatedScene scene = AnimatorApp.INSTANCE.createScene(new Random(42));
        int framerate = 25;
        for(int f = 1; f <= 8 * framerate; f++)
            scene.animate(f / (double) framerate, framerate);
        return scene;
    }
    
    @Benchmark
    public void raytracer() {
        raytracer.render(image);
        Guarded.guardReadOnly(image); // This joins the render threads
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
