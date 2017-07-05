package ch.trick17.rolezapps;

import static org.openjdk.jmh.annotations.Level.Iteration;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;
import static org.openjdk.jmh.annotations.Scope.Thread;
import static rolez.lang.Guarded.guardReadOnly;
import static rolez.lang.Guarded.guardReadWrite;
import static rolez.lang.Task.currentTask;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import rolez.lang.GuardedArray;
import rolez.lang.SliceRange;
import rolez.lang.Task;

@BenchmarkMode(SingleShotTime)
@Fork(1)
@State(Thread)
@Warmup(iterations = 30)
@Measurement(iterations = 30)
public class ArrayCopyBenchmark {
	
	@Param({"10000000", "100000000"})
	int n;
	
	SliceRange range;

	byte[] src;
	byte[] dst;
	GuardedArray<byte[]> guardedSrc;
	GuardedArray<byte[]> guardedDst;
	
	@Setup(Iteration)
	public void setup() {
        Task.registerNewRootTask();
		
		src = new byte[n];
		dst = new byte[n];
		guardedSrc = new GuardedArray<byte[]>(new byte[n]);
		guardedDst = new GuardedArray<byte[]>(new byte[n]);
		for(int i = 0; i < n; i++) {
			src[i] = (byte) i;
			guardedSrc.data[i] = (byte) i;
		}
		
		range = new SliceRange(0, n, 1);
	}
	
	@Benchmark
	public void arrayCopy() {
		System.arraycopy(src, 0, dst, 0, src.length);
	}
	
	@Benchmark
	public void forLoop() {
		byte[] src = this.src;
		byte[] dst = this.dst;
		for(int i = 0; i < src.length; i++)
			dst[i] = src[i];
	}
	
	@Benchmark
	public void forLoopWithRange() {
		byte[] src = this.src;
		byte[] dst = this.dst;
		SliceRange range = this.range;
		for(int i = range.begin; i < range.end; i += range.step)
			dst[i] = src[i];
	}
	
	@Benchmark
	public void forLoopWithContiguousRange() {
		byte[] src = this.src;
		byte[] dst = this.dst;
		SliceRange range = this.range;
		for(int i = range.begin; i < range.end; i++)
			dst[i] = src[i];
	}
	
	@Benchmark
	public void forLoopWithGuardedArrays() {
		GuardedArray<byte[]> src = this.guardedSrc;
		GuardedArray<byte[]> dst = this.guardedDst;
		for(int i = 0; i < src.data.length; i++)
			dst.data[i] = src.data[i];
	}
	
	@Benchmark
	public void forLoopSrcGuarded() {
		long $task = currentTask().idBits();
		int n = src.length;
		
		GuardedArray<byte[]> src = this.guardedSrc;
		byte[] dst = this.dst;
		for(int i = 0; i < n; i++)
			dst[i] = guardReadOnly(src, $task).data[i];
	}
	
	@Benchmark
	public void forLoopBothGuarded() {
		long $task = currentTask().idBits();
		int n = src.length;
		
		GuardedArray<byte[]> src = this.guardedSrc;
		GuardedArray<byte[]> dst = this.guardedDst;
		for(int i = 0; i < n; i++)
			guardReadWrite(dst, $task).data[i] = guardReadOnly(src, $task).data[i];
	}
	
    @TearDown(Level.Iteration)
    public void tearDown() {
        Task.unregisterRootTask();
    }

    public static void main(String[] args) throws RunnerException {
        String name = ArrayCopyBenchmark.class.getSimpleName();
        new Runner(new OptionsBuilder().include(name).build()).run();
    }
}
