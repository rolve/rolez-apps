package ch.trick17.rolezapps.kmeans;

import java.util.Random;
import java.util.concurrent.Callable;

import rolez.lang.ContiguousPartitioner;
import rolez.lang.GuardedArray;
import rolez.lang.GuardedSlice;
import rolez.lang.Task;
import rolez.lang.TaskSystem;

public class KMeansLocalOpt extends KMeans {
    
    public KMeansLocalOpt(final int dim, final int clusters, final int numTasks) {
        super(dim, clusters, numTasks);
    }
    
    @Override
    public GuardedArray<GuardedArray<double[]>[]> kMeans(
            GuardedArray<GuardedArray<double[]>[]> dataSet) {
        Random random = new Random();
        GuardedArray<GuardedArray<double[]>[]> centroids = new GuardedArray<GuardedArray<double[]>[]>(
                new GuardedArray[this.clusters]);
        for(int i = 0; i < this.clusters; i += 1)
            guardReadWrite(centroids).data[i] = this.newRandomVector(random);
        GuardedArray<int[]> assignments = new GuardedArray<int[]>(new int[dataSet.data.length]);
        boolean changed = true;
        while(changed) {
            GuardedArray<GuardedSlice<GuardedArray<double[]>[]>[]> dataParts = dataSet
                    .partition(ContiguousPartitioner.INSTANCE, this.numTasks);
            GuardedArray<GuardedSlice<int[]>[]> assignParts = assignments
                    .partition(ContiguousPartitioner.INSTANCE, this.numTasks);
            GuardedArray<Task<Boolean>[]> tasks = new GuardedArray<Task<Boolean>[]>(
                    new Task[this.numTasks]);
            for(int i = 0; i < tasks.data.length; i += 1)
                guardReadWrite(tasks).data[i] = TaskSystem.getDefault().start(this
                        .$assignTask(guardReadOnly(dataParts).data[i], centroids, guardReadOnly(
                                assignParts).data[i]));
            changed = false;
            for(int i = 0; i < tasks.data.length; i += 1)
                changed |= guardReadOnly(tasks).data[i].get();
            GuardedArray<GuardedArray<double[]>[]> newCentroids = new GuardedArray<GuardedArray<double[]>[]>(
                    new GuardedArray[this.clusters]);
            for(int i = 0; i < newCentroids.data.length; i += 1)
                guardReadWrite(newCentroids).data[i] = new GuardedArray<double[]>(
                        new double[this.dim]);
            GuardedArray<int[]> counts = new GuardedArray<int[]>(new int[this.clusters]);
            for(int i = 0; i < dataSet.data.length; i += 1) {
                GuardedArray<double[]> vector = guardReadOnly(dataSet).data[i];
                int centroidIndex = guardReadOnly(assignments).data[i];
                GuardedArray<double[]> centroid = guardReadOnly(newCentroids).data[centroidIndex];
                for(int d = 0; d < this.dim; d += 1)
                    guardReadWrite(centroid).data[d] = guardReadOnly(centroid).data[d]
                            + guardReadOnly(vector).data[d];
                guardReadWrite(counts).data[centroidIndex] = guardReadOnly(
                        counts).data[centroidIndex] + 1;
            }
            for(int i = 0; i < centroids.data.length; i += 1) {
                GuardedArray<double[]> centroid = guardReadOnly(newCentroids).data[i];
                int count = guardReadOnly(counts).data[i];
                for(int d = 0; d < this.dim; d += 1)
                    guardReadWrite(centroid).data[d] = guardReadOnly(centroid).data[d] / count;
                guardReadWrite(centroids).data[i] = centroid;
            }
        }
        return centroids;
    }
    
    @Override
    public Callable<Boolean> $assignTask(final GuardedSlice<GuardedArray<double[]>[]> dataSet,
            final GuardedArray<GuardedArray<double[]>[]> centroids,
            final GuardedSlice<int[]> assignments) {
        dataSet.share();
        centroids.share();
        assignments.pass();
        return new Callable<Boolean>() {
            public Boolean call() {
                assignments.registerNewOwner();
                try {
                    boolean changed = false;
                    for(int i = dataSet.range.begin; i < dataSet.range.end; i += dataSet.range.step) {
                        double min = Double.POSITIVE_INFINITY;
                        int minIndex = -1;
                        for(int c = 0; c < centroids.data.length; c += 1) {
                            double distance2 = distance2(guardReadOnly(dataSet)
                                    .<GuardedArray<double[]>> get(i), guardReadOnly(
                                            centroids).data[c]);
                            if(distance2 < min) {
                                min = distance2;
                                minIndex = c;
                            }
                        }
                        if(minIndex != guardReadOnly(assignments).getInt(i)) {
                            changed = true;
                            guardReadWrite(assignments).setInt(i, minIndex);
                        }
                    }
                    return changed;
                } finally {
                    dataSet.releaseShared();
                    centroids.releaseShared();
                    assignments.releasePassed();
                }
            }
        };
    }
    
    @Override
    public double distance2(GuardedArray<double[]> v1, final GuardedArray<double[]> v2) {
        double sum = 0.0;
        for(int d = 0; d < this.dim; d += 1) {
            final double diff = guardReadOnly(v1).data[d] - guardReadOnly(v2).data[d];
            sum += diff * diff;
        }
        return sum;
    }
    
    @Override
    public GuardedArray<double[]> newRandomVector(Random random) {
        GuardedArray<double[]> vec = new GuardedArray<double[]>(new double[this.dim]);
        for(int d = 0; d < this.dim; d += 1)
            guardReadWrite(vec).data[d] = random.nextDouble();
        return vec;
    }
}
