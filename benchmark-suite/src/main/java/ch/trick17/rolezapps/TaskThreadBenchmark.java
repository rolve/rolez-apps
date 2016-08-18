package ch.trick17.rolezapps;

import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.Throughput;
import static org.openjdk.jmh.annotations.Scope.Benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import rolez.lang.Task;

@BenchmarkMode(Throughput)
@Fork(1)
@Warmup(iterations = 10)
@State(Benchmark)
public class TaskThreadBenchmark {
    
    Object field;
    volatile Object volatileField;
    final ThreadLocal<Object> threadLocal = new ThreadLocal<>();
    
    @Setup(Trial)
    public void setup() {
        Task.registerNewTask();
        threadLocal.set(new Object());
    }
    
    @TearDown(Trial)
    public void tearDown() {
        Task.unregisterCurrentTask();
        threadLocal.set(null);
    }
    
    @Benchmark
    public Task<?> currentTask() {
        return Task.currentTask();
    }
    
    @Benchmark
    public Thread currentThread() {
        return Thread.currentThread();
    }
    
    @Benchmark
    public Object threadLocalAccess() {
        return threadLocal.get();
    }
    
    @Benchmark
    public Object fieldRead() {
        return field;
    }
    
    @Benchmark
    public Object volatileFieldRead() {
        return volatileField;
    }
    
    @Benchmark
    public void empty() {}
    
    public static void main(String[] args) throws RunnerException {
        String name = TaskThreadBenchmark.class.getSimpleName();
        new Runner(new OptionsBuilder().include(name).build()).run();
    }
}
