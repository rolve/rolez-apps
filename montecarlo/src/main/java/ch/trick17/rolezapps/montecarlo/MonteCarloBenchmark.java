package ch.trick17.rolezapps.montecarlo;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static ch.trick17.rolezapps.BenchmarkUtils.runAndPlot;
import static java.lang.Math.abs;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;
import static rolez.lang.Task.currentTask;

import java.util.HashMap;
import java.util.Map;

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

import classes.AppMonteCarlo;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class MonteCarloBenchmark {
    
    private static final String FILE = MonteCarloAppRolezRunner.INSTANCE.file;
    private static final int TIME_STEPS = MonteCarloAppRolezRunner.INSTANCE.steps;
    private static final Map<Integer, Double> REF_VALS = new HashMap<Integer, Double>() {{
        put( 2000, -0.034247972816469394);
        put(10000, -0.0333976656762814);
        put(60000, -0.03215796752868655);
    }};
    
    @Param({"2000", "10000", "60000"})
    int n;
    
    @Param({"1", "2", "4", "8", "16"})
    int tasks;
    
    @Param({"Checked", "Rolez", "Java"})
    String impl;
    
    MonteCarloApp app;
    AppMonteCarlo appC;
    
    @Setup(Level.Iteration)
    public void setup() {
    	if (impl.equals("Checked")) {
    		rolez.checked.lang.Task.registerNewRootTask();
    		appC = new AppMonteCarlo(TIME_STEPS, n, tasks, FILE, rolez.checked.lang.Task.currentTask().idBits());
    	} else {
	        Task.registerNewRootTask();
	        app = instantiateBenchmark(MonteCarloApp.class, impl, TIME_STEPS, n, tasks, FILE,
	                currentTask().idBits());
    	}
    }
    
    @Benchmark
    public void monteCarlo() {
    	if (impl.equals("Checked"))
    		appC.run(rolez.checked.lang.Task.currentTask().idBits());
    	else
    		app.run(currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
    	if (impl.equals("Checked")) {
    		double expectedReturnRate = appC.avgExpectedReturnRate(rolez.checked.lang.Task.currentTask().idBits());
    		double dev = abs(expectedReturnRate - REF_VALS.get(n));
    		if(dev > 1.0e-12)
	            throw new AssertionError("Validation failed");
    		
    		rolez.checked.lang.Task.unregisterRootTask();
    	} else {
	        double expectedReturnRate = app.avgExpectedReturnRate(currentTask().idBits());
	        double dev = abs(expectedReturnRate - REF_VALS.get(n));
	        if(dev > 1.0e-12)
	            throw new AssertionError("Validation failed");
	        
	        Task.unregisterRootTask();
    	}
    }

    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(MonteCarloBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        runAndPlot(options);
    }
}
