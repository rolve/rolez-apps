package ch.trick17.rolezapps.kmeans;

import static rolez.lang.GuardedArray.wrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import rolez.lang.ContiguousPartitioner;
import rolez.lang.GuardedArray;
import rolez.lang.SliceRange;
import rolez.lang.Task;
import rolez.lang.TaskSystem;

public class KMeansJava extends KMeans {
    
    public KMeansJava(final int dim, final int clusters, final int numTasks) {
        super(dim, clusters, numTasks);
    }
    
    @Override
    public GuardedArray<GuardedArray<double[]>[]> kMeans(
            GuardedArray<GuardedArray<double[]>[]> dataSet) {
        int n = dataSet.data.length;
        Random random = new Random();
        double[][] centroids = new double[clusters][];
        for(int i = 0; i < clusters; i += 1)
            centroids[i] = newRandomVectorJava(random);
            
        int[] assignments = new int[n];
        SliceRange[] ranges = ContiguousPartitioner.INSTANCE.partition(dataSet.range,
                numTasks).data;
                
        boolean changed = true;
        while(changed) {
            List<Task<Boolean>> tasks = new ArrayList<Task<Boolean>>(numTasks);
            for(int i = 0; i < numTasks; i += 1) {
                Callable<Boolean> task = $assignTask(dataSet.data, centroids, assignments,
                        ranges[i]);
                tasks.add(TaskSystem.getDefault().start(task));
            }
            
            changed = false;
            for(Task<Boolean> element : tasks)
                changed |= element.get();
                
            double[][] newCentroids = new double[clusters][];
            for(int i = 0; i < clusters; i += 1)
                newCentroids[i] = new double[dim];
                
            int[] counts = new int[clusters];
            for(int i = 0; i < n; i += 1) {
                double[] vector = dataSet.data[i].data;
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
        }
        
        GuardedArray<double[]>[] wrappedCentroids = new GuardedArray[clusters];
        for(int i = 0; i < clusters; i++)
            wrappedCentroids[i] = wrap(centroids[i]);
        return wrap(wrappedCentroids);
    }
    
    public Callable<Boolean> $assignTask(final GuardedArray<double[]>[] dataSet,
            final double[][] centroids, final int[] assignments, final SliceRange range) {
        return new Callable<Boolean>() {
            public Boolean call() {
                boolean changed = false;
                for(int i = range.begin; i < range.end; i += range.step) {
                    double min = Double.POSITIVE_INFINITY;
                    int minIndex = -1;
                    for(int c = 0; c < clusters; c += 1) {
                        double distance2 = distance2(dataSet[i].data, centroids[c]);
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
        };
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
        TaskSystem.getDefault().run(new KMeansJava(10, 10, numTasks).$mainTask(wrapped));
    }
}