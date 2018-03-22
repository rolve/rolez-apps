package ch.trick17.rolezapps;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Level.Invocation;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Scope.Benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import rolez.lang.Guarded;
import rolez.lang.GuardedArray;
import rolez.lang.Task;
import rolez.lang.TaskSystem;

@BenchmarkMode(AverageTime)
@Fork(1)
@Warmup(iterations = 10)
@OutputTimeUnit(MILLISECONDS)
@State(Benchmark)
public class RoleTransitionBenchmark {
    
    @Param({"1", "100", "10000"})
    int n;
    
    GuardedArray<Guarded[]> objects;
    
    @Setup(Invocation)
    public void setup() {
        Task.registerNewRootTask();
        objects = new GuardedArray<Guarded[]>(new Guarded[n]);
        for(int i = 0; i < objects.data.length; i++) {
            objects.data[i] = new Guarded() {};
        }
    }
    
    @TearDown(Invocation)
    public void tearDown() {
        Task.unregisterRootTask();
    }
    
    @Benchmark
    public int roleTransitions() {
        Task<Integer> task = new Task<Integer>(new Object[] {objects}, new Object[] {}) {
            protected Integer runRolez() {
                return 0;
            }
        };
        return TaskSystem.getDefault().run(task);
    }
    
    public static void main(String[] args) throws RunnerException {
        String name = RoleTransitionBenchmark.class.getSimpleName();
        new Runner(new OptionsBuilder().include(name).build()).run();
    }
}
