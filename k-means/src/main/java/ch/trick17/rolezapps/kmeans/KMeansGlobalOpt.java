package ch.trick17.rolezapps.kmeans;

import static rolez.lang.GuardedArray.wrap;

import rolez.lang.GuardedArray;
import rolez.lang.TaskSystem;

/**
 * Further optimized version that, in addition to the LocalOpt version, has no guards for method
 * arguments that are (globally) known to not need any guarding. This affects
 * {@link #distance2(GuardedArray, GuardedArray)}.
 */
public class KMeansGlobalOpt extends KMeansLocalOpt {
    
    public KMeansGlobalOpt(final int dim, final int clusters, final int numTasks) {
        super(dim, clusters, numTasks);
    }
    
    @Override
    public double distance2(GuardedArray<double[]> v1, final GuardedArray<double[]> v2) {
        double sum = 0.0;
        for(int d = 0; d < dim; d += 1) {
            final double diff = v1.data[d] - v2.data[d];
            sum += diff * diff;
        }
        return sum;
    }
    
    public static void main(final String[] args) {
        int numTasks = 1;
        GuardedArray<String[]> wrapped = wrap(args);
        TaskSystem.getDefault().run(new KMeansGlobalOpt(10, 10, numTasks).$mainTask(wrapped));
    }
}
