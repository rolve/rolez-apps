package ch.trick17.rolezapps.histogram;

import java.util.concurrent.atomic.AtomicInteger;

import ch.trick17.rolezapps.histogram.HistogramJava.Color;
import rolez.lang.ContiguousPartitioner;
import rolez.lang.GuardedArray;
import rolez.lang.SliceRange;

public class HistogramJavaAtomicInt extends Histogram {
    
    public int[][] image;
    
    public final AtomicInteger[] rHist = new AtomicInteger[256];
    public final AtomicInteger[] gHist = new AtomicInteger[256];
    public final AtomicInteger[] bHist = new AtomicInteger[256];
    
    public HistogramJavaAtomicInt(GuardedArray<GuardedArray<int[]>[]> image, long $task) {
        super($task);
        this.image = GuardedArray.unwrap(image, int[][].class);
        
        for(int c = 0; c < 256; c++) {
            rHist[c] = new AtomicInteger(0);
            gHist[c] = new AtomicInteger(0);
            bHist[c] = new AtomicInteger(0);
        }
    }
    
    @Override
    public void compute(int numTasks, long $task) {
        SliceRange[] ranges = ContiguousPartitioner.INSTANCE.partition(new SliceRange(0, image.length, 1), numTasks, 0);
        
        Thread[] threads = new Thread[numTasks - 1];
        for(int i = 0; i < numTasks - 1; i++) {
            Thread thread = new Thread(computePartTask(ranges[i + 1]));
            threads[i] = thread;
            thread.start();
        }
        computePart(ranges[0]);
        
        for(int i = 0; i < numTasks - 1; i++)
            try {
                threads[i].join();
            } catch(InterruptedException e) {
                throw new AssertionError(e);
            }
    }
    
    private Runnable computePartTask(final SliceRange range) {
        return new Runnable() {
            public void run() {
                computePart(range);
            }
        };
    }

    private void computePart(SliceRange range) {
        for(int y = range.begin; y < range.end; y += range.step) {
            int[] row = image[y];
            for(int c : row) {
                Color color = new Color(c);
                rHist[color.r].incrementAndGet();
                gHist[color.g].incrementAndGet();
                bHist[color.b].incrementAndGet();
            }
        }
    }
}
