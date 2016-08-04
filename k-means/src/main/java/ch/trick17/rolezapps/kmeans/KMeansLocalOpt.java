package ch.trick17.rolezapps.kmeans;

import static rolez.lang.GuardedArray.wrap;

import java.util.Random;
import java.util.concurrent.Callable;

import rolez.lang.ContiguousPartitioner;
import rolez.lang.GuardedArray;
import rolez.lang.GuardedSlice;
import rolez.lang.Task;
import rolez.lang.TaskSystem;

/**
 * A manually optimized version of {@link KMeans}. Guarding ops were moved out of loops and removed
 * if a method-local analysis could deduce that guarding is not necessary, under the assumption that
 * the analysis knows which methods can start tasks.
 */
public class KMeansLocalOpt extends KMeans {
    
    public KMeansLocalOpt(final int dim, final int clusters, final int numTasks) {
        super(dim, clusters, numTasks);
    }
    
    @Override
    public GuardedArray<GuardedArray<double[]>[]> kMeans(
            GuardedArray<GuardedArray<double[]>[]> dataSet, int maxIterations) {
        Random random = new Random();
        GuardedArray<GuardedArray<double[]>[]> centroids = new GuardedArray<GuardedArray<double[]>[]>(
                new GuardedArray[clusters]);
        for(int i = 0; i < clusters; i += 1)
            centroids.data[i] = newRandomVector(random);
        
        GuardedArray<int[]> assignments = new GuardedArray<int[]>(new int[dataSet.data.length]);
        
        int iterations = 0;
        boolean changed = true;
        while(changed && iterations < maxIterations) {
            GuardedArray<GuardedSlice<GuardedArray<double[]>[]>[]> dataParts = dataSet
                    .partition(ContiguousPartitioner.INSTANCE, numTasks);
            GuardedArray<GuardedSlice<int[]>[]> assignParts = assignments
                    .partition(ContiguousPartitioner.INSTANCE, numTasks);
            
            GuardedArray<Task<Boolean>[]> tasks = new GuardedArray<Task<Boolean>[]>(
                    new Task[numTasks]);
            for(int i = 0; i < tasks.data.length; i += 1)
                tasks.data[i] = TaskSystem.getDefault().start($assignTask(dataParts.data[i],
                        centroids, assignParts.data[i]));
            
            changed = false;
            for(Task<Boolean> element : tasks.data)
                changed |= element.get();
            
            GuardedArray<GuardedArray<double[]>[]> newCentroids = new GuardedArray<GuardedArray<double[]>[]>(
                    new GuardedArray[clusters]);
            for(int i = 0; i < newCentroids.data.length; i += 1)
                newCentroids.data[i] = new GuardedArray<double[]>(new double[dim]);
            
            GuardedArray<int[]> counts = new GuardedArray<int[]>(new int[clusters]);
            guardReadOnly(dataSet);
            guardReadOnly(assignments);
            for(int i = 0; i < dataSet.data.length; i += 1) {
                GuardedArray<double[]> vector = dataSet.data[i];
                int centroidIndex = assignments.data[i];
                GuardedArray<double[]> centroid = newCentroids.data[centroidIndex];
                for(int d = 0; d < dim; d += 1)
                    centroid.data[d] = centroid.data[d] + vector.data[d];
                counts.data[centroidIndex]++;
            }
            
            guardReadWrite(centroids);
            for(int i = 0; i < centroids.data.length; i += 1) {
                GuardedArray<double[]> centroid = newCentroids.data[i];
                int count = counts.data[i];
                for(int d = 0; d < dim; d += 1)
                    centroid.data[d] /= count;
                centroids.data[i] = centroid;
            }
            iterations++;
        }
        return centroids;
    }
    
    @Override
    public Task<Boolean> $assignTask(final GuardedSlice<GuardedArray<double[]>[]> dataSet,
            final GuardedArray<GuardedArray<double[]>[]> centroids,
            final GuardedSlice<int[]> assignments) {
        Task<Boolean> task = new Task<>(new Callable<Boolean>() {
            public Boolean call() {
                assignments.completePass();
                try {
                    boolean changed = false;
                    for(int i = dataSet.range.begin; i < dataSet.range.end; i += dataSet.range.step) {
                        double min = Double.POSITIVE_INFINITY;
                        int minIndex = -1;
                        for(int c = 0; c < centroids.data.length; c += 1) {
                            double distance2 = distance2(dataSet.<GuardedArray<double[]>> get(i),
                                    centroids.data[c]);
                            if(distance2 < min) {
                                min = distance2;
                                minIndex = c;
                            }
                        }
                        if(minIndex != assignments.getInt(i)) {
                            changed = true;
                            assignments.setInt(i, minIndex);
                        }
                    }
                    return changed;
                } finally {
                    dataSet.releaseShared();
                    centroids.releaseShared();
                    assignments.releasePassed();
                }
            }
        });
        dataSet.share(task);
        centroids.share(task);
        assignments.pass(task);
        return task;
    }
    
    @Override
    public double distance2(GuardedArray<double[]> v1, final GuardedArray<double[]> v2) {
        double sum = 0.0;
        guardReadOnly(v1);
        guardReadOnly(v2);
        for(int d = 0; d < dim; d += 1) {
            final double diff = v1.data[d] - v2.data[d];
            sum += diff * diff;
        }
        return sum;
    }
    
    @Override
    public GuardedArray<double[]> newRandomVector(Random random) {
        GuardedArray<double[]> vec = new GuardedArray<double[]>(new double[dim]);
        for(int d = 0; d < dim; d += 1)
            vec.data[d] = random.nextDouble();
        return vec;
    }
    
    public static void main(final String[] args) {
        int numTasks = 1;
        GuardedArray<String[]> wrapped = wrap(args);
        TaskSystem.getDefault().run(new KMeansLocalOpt(10, 10, numTasks).$mainTask(wrapped));
    }
}
