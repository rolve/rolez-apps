package ch.trick17.rolezapps.kmeans;

import static rolez.lang.GuardedArray.wrap;

import java.util.Random;

import rolez.lang.ContiguousPartitioner;
import rolez.lang.GuardedArray;
import rolez.lang.GuardedSlice;
import rolez.lang.GuardedVectorBuilder;
import rolez.lang.Task;
import rolez.lang.TaskSystem;

/**
 * A manually optimized version of {@link KMeans}. Guarding ops were moved out of loops and removed
 * if a method-local analysis could deduce that guarding is not necessary, under the assumption that
 * the analysis knows which methods can start tasks.
 */
public class KMeansRolezLocalOpt extends KMeansRolez {
    
    public KMeansRolezLocalOpt(int dim, int clusters, int numTasks, long $task) {
        super(dim, clusters, numTasks, $task);
    }
    
    @Override
    public GuardedArray<double[][]> kMeans(GuardedArray<double[][]> dataSet, int maxIterations,
            long $task) {
        Random random = new Random();
        GuardedArray<double[][]> centroids = new GuardedArray<double[][]>(new double[clusters][]);
        for(int i = 0; i < clusters; i += 1)
            centroids.data[i] = newRandomVector(random, $task);
        
        GuardedArray<int[]> assignments = new GuardedArray<int[]>(new int[dataSet.data.length]);
        
        int iterations = 0;
        boolean changed = true;
        while(changed && iterations < maxIterations) {
            GuardedArray<GuardedSlice<double[][]>[]> dataParts = dataSet
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
            
            GuardedArray<GuardedVectorBuilder<double[]>[]> newCentroids = new GuardedArray<GuardedVectorBuilder<double[]>[]>(
                    new GuardedVectorBuilder[clusters]);
            for(int i = 0; i < newCentroids.data.length; i += 1)
                newCentroids.data[i] = new GuardedVectorBuilder<double[]>(new double[dim]);
            
            GuardedArray<int[]> counts = new GuardedArray<int[]>(new int[clusters]);
            guardReadOnly(dataSet, $task);
            guardReadOnly(assignments, $task);
            for(int i = 0; i < dataSet.data.length; i += 1) {
                double[] vector = dataSet.data[i];
                int centroidIndex = assignments.data[i];
                GuardedVectorBuilder<double[]> centroid = newCentroids.data[centroidIndex];
                for(int d = 0; d < dim; d += 1)
                    centroid.data[d] = centroid.data[d] + vector[d];
                counts.data[centroidIndex]++;
            }
            
            guardReadWrite(centroids, $task);
            for(int i = 0; i < centroids.data.length; i += 1) {
                GuardedVectorBuilder<double[]> centroid = newCentroids.data[i];
                int count = counts.data[i];
                for(int d = 0; d < dim; d += 1)
                    centroid.data[d] /= count;
                centroids.data[i] = centroid.build();
            }
            iterations++;
        }
        return centroids;
    }
    
    @Override
    public Task<Boolean> $assignTask(final GuardedSlice<double[][]> dataSet,
            final GuardedArray<double[][]> centroids,
            final GuardedSlice<int[]> assignments) {
        return new Task<Boolean>(new Object[]{assignments}, new Object[]{dataSet, centroids}) {
            @Override
            protected Boolean runRolez() {
                long $task = idBits();
                boolean changed = false;
                for(int i = dataSet.range.begin; i < dataSet.range.end; i += dataSet.range.step) {
                    double min = Double.POSITIVE_INFINITY;
                    int minIndex = -1;
                    for(int c = 0; c < centroids.data.length; c += 1) {
                        double distance2 = distance2(dataSet.<double[]> get(i), centroids.data[c],
                                $task);
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
            }
        };
    }
    
    @Override
    public double[] newRandomVector(Random random, long $task) {
        final GuardedVectorBuilder<double[]> vec = new GuardedVectorBuilder<>(new double[this.dim]);
        for(int d = 0; d < dim; d += 1)
            vec.setDouble(d, random.nextDouble());
        return vec.build();
    }
    
    public static void main(final String[] args) {
        int numTasks = 1;
        GuardedArray<String[]> wrapped = wrap(args);
        TaskSystem.getDefault().run(new KMeansRolezLocalOpt(10, 10, numTasks, 0L).$mainTask(wrapped));
    }
}
