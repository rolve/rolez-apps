package ch.trick17.rolezapps.mergesort;

import static ch.trick17.rolezapps.BenchmarkUtils.instantiateBenchmark;
import static ch.trick17.rolezapps.BenchmarkUtils.runAndPlot;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;
import static rolez.lang.Task.currentTask;

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

import classes.AppMergesort;
import rolez.checked.lang.CheckedArray;
import rolez.lang.GuardedArray;
import rolez.lang.MathExtra;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
public class MergesortBenchmark {
    
    @Param({"300000", "1500000", "6000000"})
    int n;
    
    @Param({"Checked", "Rolez", "RolezL", "Java"})
    String impl;
    
    @Param({"1", "2", "4", "8", "16", "32"})
    int tasks;
    
    Mergesort mergesort;
    AppMergesort mergesortC;
    GuardedArray<int[]> data1;
    GuardedArray<int[]> data2;
    GuardedArray<int[]> data3;
    CheckedArray<int[]> cData1;
    CheckedArray<int[]> cData2;
    CheckedArray<int[]> cData3;
    
    @Setup(Level.Iteration)
    public void setup() {
        Task.registerNewRootTask();
        Random random = new Random(42);
        int maxLevel = MathExtra.INSTANCE.log2(tasks, currentTask().idBits());
        if (impl.equals("Checked")) {
            rolez.checked.lang.Task.registerNewRootTask();
            mergesortC = new AppMergesort(maxLevel, rolez.checked.lang.Task.currentTask().idBits());
            cData1 = mergesortC.shuffledInts(n, random, rolez.checked.lang.Task.currentTask().idBits());
            cData2 = mergesortC.shuffledInts(n, random, rolez.checked.lang.Task.currentTask().idBits());
            cData3 = mergesortC.shuffledInts(n, random, rolez.checked.lang.Task.currentTask().idBits());
            return;
        }
        mergesort = instantiateBenchmark(Mergesort.class, impl, maxLevel, currentTask().idBits());
        data1 = mergesort.shuffledInts(n, random, currentTask().idBits());
        data2 = mergesort.shuffledInts(n, random, currentTask().idBits());
        data3 = mergesort.shuffledInts(n, random, currentTask().idBits());
    }
    
    @Benchmark
    public void mergesort() {
    	if (impl.equals("Checked")) {
    		mergesortC.sort(cData1, rolez.checked.lang.Task.currentTask().idBits());
    		mergesortC.sort(cData2, rolez.checked.lang.Task.currentTask().idBits());
    		mergesortC.sort(cData3, rolez.checked.lang.Task.currentTask().idBits());
    		return;
    	}
        mergesort.sort(data1, currentTask().idBits());
        mergesort.sort(data2, currentTask().idBits());
        mergesort.sort(data3, currentTask().idBits());
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() {
    	if (impl.equals("Checked")) {
            rolez.checked.lang.Task.unregisterRootTask();
            if (!isSorted(cData1.getUncheckedArrayRead()) || !isSorted(cData2.getUncheckedArrayRead()) || !isSorted(cData3.getUncheckedArrayRead())) {
    	    	Task.unregisterRootTask();
            	throw new AssertionError("Array not sorted!");
            }
        	Task.unregisterRootTask();
    	} else {
    		if (!isSorted(data1.data) || !isSorted(data2.data) || !isSorted(data3.data)) {
    	    	Task.unregisterRootTask();
            	throw new AssertionError("Array not sorted!");
    		}
    		Task.unregisterRootTask();
    	}
    }
    
    private boolean isSorted(int[] array) {
    	for (int i : array) {
    		if (array[i] != i) return false;
    	}
    	return true;
    }
    
    public static void main(String[] args) {
        Options options = new OptionsBuilder().include(MergesortBenchmark.class.getSimpleName())
                .warmupIterations(10).measurementIterations(30).build();
        runAndPlot(options);
    }
}
