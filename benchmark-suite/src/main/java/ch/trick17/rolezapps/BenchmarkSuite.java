package ch.trick17.rolezapps;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkSuite {
    
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(".*")
                .warmupIterations(20).measurementIterations(30).build();
        new Runner(options).run();
    }
}
