package ch.trick17.rolezapps.quicksort;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import rolez.lang.GuardedArray;
import rolez.lang.TaskSystem;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
public class QuicksortBenchmark {
    
    @Param({"100", "1000", "10000", "100000"})
    int n;
    
    Quicksort quicksort;
    GuardedArray<int[]> data;
    
    @Setup(Level.Invocation)
    public void setup() {
        quicksort = new Quicksort();
        data = quicksort.shuffledInts(n);
    }
    
    @Benchmark
    public void quicksort() {
        TaskSystem.getDefault().run(quicksort.$sortTask(data));
    }
    
    public static void main(String[] args) throws RunnerException {
        /* When invoked from Maven, the java.class.path property, which JMH uses to start forked
         * JVMs, does not include project dependencies */
        System.setProperty("java.class.path", actualClasspath());
        
        Options options = new OptionsBuilder().include(QuicksortBenchmark.class.getSimpleName())
                .forks(1).jvmArgs("-server").build();
        new Runner(options).run();
    }
    
    @SuppressWarnings("resource")
    private static String actualClasspath() {
        URLClassLoader classLoader = (URLClassLoader) QuicksortBenchmark.class.getClassLoader();
        StringBuilder classpath = new StringBuilder();
        for(URL url : classLoader.getURLs())
            classpath.append(url.getPath()).append(File.pathSeparator);
        return classpath.toString();
    }
}
