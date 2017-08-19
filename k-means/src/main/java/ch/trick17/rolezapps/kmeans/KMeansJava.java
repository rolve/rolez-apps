package ch.trick17.rolezapps.kmeans;

import static java.lang.Double.POSITIVE_INFINITY;
import static rolez.lang.GuardedArray.wrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import rolez.lang.ContiguousPartitioner;
import rolez.lang.GuardedArray;
import rolez.lang.SliceRange;
import rolez.lang.TaskSystem;

public class KMeansJava extends KMeansRolez {
    
    public KMeansJava(int dim, int clusters, int numTasks, long $task) {
        super(dim, clusters, numTasks, $task);
    }
    
    @Override
    public GuardedArray<double[][]> kMeans(GuardedArray<double[][]> dataSet, int maxIterations,
            long $task) {
        int n = dataSet.data.length;
        Random random = new Random();
        double[][] centroids = new double[clusters][];
        for(int i = 0; i < clusters; i++)
            centroids[i] = newRandomVectorJava(random);
        
        int[] assignments = new int[n];
        SliceRange[] ranges = ContiguousPartitioner.INSTANCE.partition(dataSet.range, numTasks);
        
        int iterations = 0;
        boolean changed = true;
        while(changed && iterations < maxIterations) {
            List<FutureTask<Result>> tasks = new ArrayList<FutureTask<Result>>(numTasks);
            for(int i = 0; i < numTasks; i++) {
                FutureTask<Result> task = $assignAndUpdateTask(dataSet.data, centroids,
                        assignments, ranges[i]);
                tasks.add(task);
                new Thread(task).start();
            }
            
            changed = false;
            double[][] newCentroids = new double[clusters][];
            for(int c = 0; c < clusters; c++)
                newCentroids[c] = new double[dim];
            int[] counts = new int[clusters];
            
            for(FutureTask<Result> task : tasks)
                try {
                    Result result = task.get();
                    changed |= result.changed;
                    for(int c = 0; c < clusters; c++) {
                        for(int d = 0; d < dim; d++)
                            newCentroids[c][d] += result.centroids[c][d];
                        counts[c] += result.counts[c];
                    }
                } catch(InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            
            for(int c = 0; c < clusters; c++) {
                double[] centroid = newCentroids[c];
                int count = counts[c];
                for(int d = 0; d < dim; d++)
                    centroid[d] /= count;
                centroids[c] = centroid;
            }
            iterations++;
        }
        
        GuardedArray<double[]>[] wrappedCentroids = new GuardedArray[clusters];
        for(int i = 0; i < clusters; i++)
            wrappedCentroids[i] = wrap(centroids[i]);
        return wrap(wrappedCentroids);
    }
    
    public FutureTask<Result> $assignAndUpdateTask(final double[][] dataSet,
            final double[][] centroids, final int[] assignments, final SliceRange range) {
        return new FutureTask<>(new Callable<Result>() {
            public Result call() {
                boolean changed = false;
                double[][] newCentroids = new double[clusters][];
                for(int i = 0; i < clusters; i++)
                    newCentroids[i] = new double[dim];
                int[] counts = new int[clusters];
                
                for(int i = range.begin; i < range.end; i += range.step) {
                    double[] vector = dataSet[i];
                    double min = POSITIVE_INFINITY;
                    int cluster = -1;
                    for(int c = 0; c < clusters; c++) {
                        double distance2 = distance2(vector, centroids[c]);
                        if(distance2 < min) {
                            min = distance2;
                            cluster = c;
                        }
                    }
                    if(cluster != assignments[i]) {
                        changed = true;
                        assignments[i] = cluster;
                    }
                    double[] newCentroid = newCentroids[cluster];
                    for(int d = 0; d < dim; d++)
                        newCentroid[d] += vector[d];
                    counts[cluster]++;
                }
                return new Result(changed, newCentroids, counts);
            }
        });
    }
    
    private static class Result {
        final boolean changed;
        final double[][] centroids;
        final int[] counts;
        
        public Result(boolean changed, double[][] centroids, int[] counts) {
            this.changed = changed;
            this.centroids = centroids;
            this.counts = counts;
        }
    }
    
    public double distance2(double[] data, final double[] centroids) {
        double sum = 0.0;
        for(int d = 0; d < dim; d++) {
            final double diff = data[d] - centroids[d];
            sum += diff * diff;
        }
        return sum;
    }
    
    public double[] newRandomVectorJava(Random random) {
        double[] vec = new double[dim];
        for(int d = 0; d < dim; d++)
            vec[d] = random.nextDouble();
        return vec;
    }
    
    public static void main(final String[] args) {
        int numTasks = 1;
        GuardedArray<String[]> wrapped = wrap(args);
        TaskSystem.getDefault().run(new KMeansJava(10, 10, numTasks, 0L).main$Task(wrapped));
    }
}
