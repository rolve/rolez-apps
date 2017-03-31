package ch.trick17.rolezapps.histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import rolez.lang.ContiguousPartitioner;
import rolez.lang.GuardedArray;
import rolez.lang.SliceRange;

public class HistogramJava extends Histogram {
    
    public int[][] image;
    
    public int[] rHist;
    public int[] gHist;
    public int[] bHist;
    
    public HistogramJava(GuardedArray<GuardedArray<int[]>[]> image, long $task) {
        super(image, $task);
        this.image = GuardedArray.unwrap(image, int[][].class);
    }
    
    @Override
    public void compute(int numTasks, long $task) {
        SliceRange[] ranges = ContiguousPartitioner.INSTANCE.partition(new SliceRange(0, image.length, 1), numTasks, 0);
        
        List<Future<HistPart>> tasks = new ArrayList<>();
        for(int i = 0; i < numTasks - 1; i++) {
            FutureTask<HistPart> task = computePartTask(ranges[i + 1]);
            tasks.add(task);
            new Thread(task).start();
        }
        HistPart part0 = computePart(ranges[0]);
        
        rHist = part0.r;
        gHist = part0.g;
        bHist = part0.b;
        for(int i = 0; i < (numTasks - 1); i++)
            try {
                this.merge(tasks.get(i).get());
            } catch(InterruptedException | ExecutionException e) {
                throw new AssertionError(e);
            }
    }
    
    private FutureTask<HistPart> computePartTask(final SliceRange range) {
        return new FutureTask<HistPart>(new Callable<HistPart>() {
            public HistPart call() {
                return computePart(range);
            }
        });
    }

    private HistPart computePart(SliceRange range) {
        int[] r = new int[256];
        int[] g = new int[256];
        int[] b = new int[256];
        for(int y = range.begin; y < range.end; y += range.step) {
            int[] row = image[y];
            for(int element : row) {
                Color color = new Color(element);
                r[color.r]++;
                g[color.g]++;
                b[color.b]++;
            }
        }
        return new HistPart(r, g, b);
    }
    
    private void merge(HistPart histPart) {
        for(int c = 0; c < 256; c++) {
            this.rHist[c] += histPart.r[c];
            this.gHist[c] += histPart.g[c];
            this.bHist[c] += histPart.b[c];
        }
    }
    
    public static class HistPart {
        public final int[] r;
        public final int[] g;
        public final int[] b;
        
        public HistPart(int[] r, int[] g, int[] b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
    
    public static class Color {
        public final int r;
        public final int g;
        public final int b;
        
        public Color(int rgb) {
            this.r = (rgb >> 16) & 255;
            this.g = (rgb >> 8) & 255;
            this.b = rgb & 255;
        }
    }
    
}
