package ch.trick17.rolezapps.kmeans;

public class KMeansRolezOpt extends KMeansRolez {

    public KMeansRolezOpt(final int dim, final int clusters, final int numTasks, final long $task) {
        super(dim, clusters, numTasks, $task);
    }
    
    @Override
    public rolez.lang.GuardedArray<double[][]> kMeans$Unguarded(final rolez.lang.GuardedArray<double[][]> dataSet, final int maxIterations, final long $task) {
        final java.util.Random random = new java.util.Random();
        final rolez.lang.GuardedArray<double[][]> centroids = new rolez.lang.GuardedArray<double[][]>(new double[this.clusters][]);
        for(int i = 0; i < this.clusters; i++)
            centroids.data[i] = this.newRandomVector$Unguarded(random, $task);
        final rolez.lang.SliceRange[] ranges = rolez.lang.ContiguousPartitioner.INSTANCE.partition$Unguarded(dataSet.range, this.numTasks, $task);
        final rolez.lang.GuardedArray<rolez.lang.GuardedArray<int[]>[]> assignments = new rolez.lang.GuardedArray<rolez.lang.GuardedArray<int[]>[]>(new rolez.lang.GuardedArray[this.numTasks]);
        for(int i = 0; i < this.numTasks; i++)
            assignments.data[i] = new rolez.lang.GuardedArray<int[]>(new int[dataSet.data.length]);
        int iterations = 0;
        boolean changed = true;
        while(changed && (iterations < maxIterations)) {
            final rolez.lang.GuardedSlice<ch.trick17.rolezapps.kmeans.Result[]> results = new rolez.lang.GuardedArray<ch.trick17.rolezapps.kmeans.Result[]>(new ch.trick17.rolezapps.kmeans.Result[this.numTasks]);
            { /* parfor */
                final java.util.List<java.lang.Object[]> $argsList = new java.util.ArrayList<>();
                for(int i = 0; i < results.range.size(); i++)
                    $argsList.add(new java.lang.Object[] {this, dataSet, centroids, assignments.data[i], ranges[i], results.slice(i, i + 1), i});
                
                final java.lang.Object[][] $passed = new java.lang.Object[$argsList.size()][];
                final java.lang.Object[][] $shared = new java.lang.Object[$argsList.size()][];
                for(int $i = 0; $i < $argsList.size(); $i++) {
                    final java.lang.Object[] $args = $argsList.get($i);
                    $passed[$i] = new java.lang.Object[] {$args[3], $args[5]};
                    $shared[$i] = new java.lang.Object[] {$args[1], $args[2]};
                }
                
                final rolez.lang.Task<?>[] $tasks = new rolez.lang.Task<?>[$argsList.size()];
                long $tasksBits = 0;
                for(int $i = 0; $i < $tasks.length; $i++) {
                    final java.lang.Object[] $args = $argsList.get($i);
                    $tasks[$i] = new rolez.lang.Task<java.lang.Void>($passed[$i], $shared[$i], $tasksBits, true) {
                        @java.lang.Override
                        protected java.lang.Void runRolez() {
                            ((ch.trick17.rolezapps.kmeans.KMeansRolezOpt) $args[0]).assignAndUpdate$Unguarded((rolez.lang.GuardedArray<double[][]>) $args[1], (rolez.lang.GuardedArray<double[][]>) $args[2], (rolez.lang.GuardedArray<int[]>) $args[3], (rolez.lang.SliceRange) $args[4], (rolez.lang.GuardedSlice<ch.trick17.rolezapps.kmeans.Result[]>) $args[5], (int) $args[6], idBits());
                            return null;
                        }
                    };
                    $tasksBits |= $tasks[$i].idBits();
                }
                
                try {
                    for(int $i = 0; $i < $tasks.length-1; $i++)
                        rolez.lang.TaskSystem.getDefault().start($tasks[$i]);
                    rolez.lang.TaskSystem.getDefault().run($tasks[$tasks.length - 1]);
                } finally {
                    for(rolez.lang.Task<?> $t : $tasks)
                        $t.get();
                }
            }
            
            changed = false;
            final rolez.lang.GuardedArray<rolez.lang.GuardedVectorBuilder<double[]>[]> newCentroids = new rolez.lang.GuardedArray<rolez.lang.GuardedVectorBuilder<double[]>[]>(new rolez.lang.GuardedVectorBuilder[this.clusters]);
            for(int i = 0; i < newCentroids.data.length; i++)
                newCentroids.data[i] = new rolez.lang.GuardedVectorBuilder<double[]>(new double[this.dim]);
            final rolez.lang.GuardedArray<int[]> counts = new rolez.lang.GuardedArray<int[]>(new int[this.clusters]);
            for(int i = 0; i < results.range.size(); i++) {
                final ch.trick17.rolezapps.kmeans.Result result = results.<ch.trick17.rolezapps.kmeans.Result>get(i);
                changed |= result.changed;
                for(int c = 0; c < this.clusters; c++) {
                    final rolez.lang.GuardedVectorBuilder<double[]> newCentroid = newCentroids.data[c];
                    final double[] resultCentroid = result.centroids[c];
                    for(int d = 0; d < this.dim; d++)
                        newCentroid.setDouble(d, newCentroid.data[d] + resultCentroid[d]);
                    counts.data[c] = counts.data[c] + result.counts[c];
                }
            }
            for(int c = 0; c < this.clusters; c++) {
                final rolez.lang.GuardedVectorBuilder<double[]> centroid = newCentroids.data[c];
                final int count = counts.data[c];
                for(int d = 0; d < this.dim; d++)
                    centroid.setDouble(d, centroid.data[d] / count);
                centroids.data[c] = centroid.build();
            }
            iterations++;
        }
        return centroids;
    }
}
