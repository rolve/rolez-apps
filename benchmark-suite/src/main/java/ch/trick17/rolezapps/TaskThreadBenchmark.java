package ch.trick17.rolezapps;

import static org.openjdk.jmh.annotations.Mode.Throughput;

import java.util.concurrent.Callable;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import rolez.lang.Task;
import rolez.lang.TaskSystem;

@BenchmarkMode(Throughput)
@Fork(1)
public class TaskThreadBenchmark {
    
    @Benchmark
    public Task<?> currentTask() {
        return TaskSystem.getDefault().run(new Callable<Task<?>>() {
            public Task<?> call() throws Exception {
                return Task.currentTask();
            }
        });
    }
    
    @Benchmark
    public Thread currentThread() {
        return TaskSystem.getDefault().run(new Callable<Thread>() {
            public Thread call() throws Exception {
                return Thread.currentThread();
            }
        });
    }
    
    public static void main(String[] args) throws RunnerException {
        String name = TaskThreadBenchmark.class.getSimpleName();
        new Runner(new OptionsBuilder().include(name).build()).run();
    }
}
