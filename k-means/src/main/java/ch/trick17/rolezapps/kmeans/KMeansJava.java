package ch.trick17.rolezapps.kmeans;

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

public class KMeansJava extends KMeans {
    
    public KMeansJava(int dim, int clusters, int numTasks, long $task) {
        super(dim, clusters, numTasks, $task);
    }
    
    @Override
    public GuardedArray<double[][]> kMeans(GuardedArray<double[][]> dataSet, int maxIterations,
            long $task) {
        int n = dataSet.data.length;
        Random random = new Random();
        double[][] centroids = new double[clusters][];
        for(int i = 0; i < clusters; i += 1)
            centroids[i] = newRandomVectorJava(random);
        
        int[] assignments = new int[n];
        SliceRange[] ranges = ContiguousPartitioner.INSTANCE.partition(dataSet.range,
                numTasks).data;
        
        int iterations = 0;
        boolean changed = true;
        while(changed && iterations < maxIterations) {
            List<FutureTask<Boolean>> tasks = new ArrayList<FutureTask<Boolean>>(numTasks);
            for(int i = 0; i < numTasks; i += 1) {
                FutureTask<Boolean> task = $assignTask(dataSet.data, centroids, assignments,
                        ranges[i]);
                tasks.add(task);
                new Thread(task).start();
            }
            
            changed = false;
            for(FutureTask<Boolean> task : tasks)
                try {
                    changed |= task.get();
                } catch(InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            
            double[][] newCentroids = new double[clusters][];
            for(int i = 0; i < clusters; i += 1)
                newCentroids[i] = new double[dim];
            
            int[] counts = new int[clusters];
            for(int i = 0; i < n; i += 1) {
                double[] vector = dataSet.data[i];
                int centroidIndex = assignments[i];
                double[] centroid = newCentroids[centroidIndex];
                for(int d = 0; d < dim; d += 1)
                    centroid[d] += vector[d];
                counts[centroidIndex]++;
            }
            
            for(int i = 0; i < clusters; i += 1) {
                double[] centroid = newCentroids[i];
                int count = counts[i];
                for(int d = 0; d < dim; d += 1)
                    centroid[d] /= count;
                centroids[i] = centroid;
            }
            iterations++;
        }
        
        GuardedArray<double[]>[] wrappedCentroids = new GuardedArray[clusters];
        for(int i = 0; i < clusters; i++)
            wrappedCentroids[i] = wrap(centroids[i]);
        return wrap(wrappedCentroids);
    }
    
    public FutureTask<Boolean> $assignTask(final double[][] dataSet,
            final double[][] centroids, final int[] assignments, final SliceRange range) {
        return new FutureTask<>(new Callable<Boolean>() {
            public Boolean call() {
                boolean changed = false;
                for(int i = range.begin; i < range.end; i += range.step) {
                    double min = Double.POSITIVE_INFINITY;
                    int minIndex = -1;
                    for(int c = 0; c < clusters; c += 1) {
                        double distance2 = distance2(dataSet[i], centroids[c]);
                        if(distance2 < min) {
                            min = distance2;
                            minIndex = c;
                        }
                    }
                    if(minIndex != assignments[i]) {
                        changed = true;
                        assignments[i] = minIndex;
                    }
                }
                return changed;
            }
        });
    }
    
    public double distance2(double[] data, final double[] centroids) {
        double sum = 0.0;
        for(int d = 0; d < dim; d += 1) {
            final double diff = data[d] - centroids[d];
            sum += diff * diff;
        }
        return sum;
    }
    
    public double[] newRandomVectorJava(Random random) {
        double[] vec = new double[dim];
        for(int d = 0; d < dim; d += 1)
            vec[d] = random.nextDouble();
        return vec;
    }
    
    public static void main(final String[] args) {
        int numTasks = 1;
        GuardedArray<String[]> wrapped = wrap(args);
        TaskSystem.getDefault().run(new KMeansJava(10, 10, numTasks, 0L).$mainTask(wrapped));
    }
}
