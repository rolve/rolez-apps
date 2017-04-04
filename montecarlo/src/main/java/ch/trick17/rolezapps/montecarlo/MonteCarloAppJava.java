/**************************************************************************
 *                                                                         *
 *         Java Grande Forum Benchmark Suite - Thread Version 1.0          *
 *                                                                         *
 *                            produced by                                  *
 *                                                                         *
 *                  Java Grande Benchmarking Project                       *
 *                                                                         *
 *                                at                                       *
 *                                                                         *
 *                Edinburgh Parallel Computing Centre                      *
 *                                                                         *
 *                email: epcc-javagrande@epcc.ed.ac.uk                     *
 *                                                                         *
 *      Original version of this code by Hon Yau (hwyau@epcc.ed.ac.uk)     *
 *                                                                         *
 *      This version copyright (c) The University of Edinburgh, 2001.      *
 *                         All rights reserved.                            *
 *                                                                         *
 **************************************************************************/

package ch.trick17.rolezapps.montecarlo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import ch.trick17.rolezapps.montecarlojava.MonteCarloPath;
import ch.trick17.rolezapps.montecarlojava.RatePath;
import ch.trick17.rolezapps.montecarlojava.Returns;
import rolez.lang.ContiguousPartitioner;
import rolez.lang.SliceRange;

/**
 * Code, a test-harness for invoking and driving the Applications Demonstrator
 * classes.
 * <p>
 * To do:
 * <ol>
 * <li>Very long delay prior to connecting to the server.</li>
 * <li>Some text output seem to struggle to get out, without the user tapping
 * ENTER on the keyboard!</li>
 * </ol>
 *
 * @author H W Yau
 * @version $Revision: 1.12 $ $Date: 1999/02/16 19:13:38 $
 */
public class MonteCarloAppJava extends MonteCarloApp {
    
    private static final double PATH_START_VALUE = 100.0;
    
    private final Returns returns;
    
    private final long[] seeds;
    private final List<Double> results = new ArrayList<>();

    public MonteCarloAppJava(int steps, int runs, int numTasks, String ratesFile, long $task) {
        super(steps, runs, numTasks, $task);
        
        returns = new Returns(RatePath.readRatesFile(ratesFile));
        
        seeds = new long[runs];
        for(int i = 0; i < runs; i++)
            seeds[i] = i * 11;
    }
    
    @Override
    public void run(long $task) {
        SliceRange fullRange = new SliceRange(0, runs, 1);
        SliceRange[] ranges = ContiguousPartitioner.INSTANCE.partition(fullRange, numTasks);
        
        List<FutureTask<List<Double>>> tasks = new ArrayList<FutureTask<List<Double>>>();
        for(int i = 1; i < numTasks; i++) {
            FutureTask<List<Double>> task = new FutureTask<>(new MonteCarloTask(ranges[i]));
            new Thread(task).start();
            tasks.add(task);
        }
        List<Double> ownResults = new MonteCarloTask(ranges[0]).call();
        results.addAll(ownResults);
        
        for(FutureTask<List<Double>> task : tasks)
            try {
                results.addAll(task.get());
            } catch(final InterruptedException | ExecutionException e) {
                throw new AssertionError(e);
            }
    }
    
    /**
     * Method for doing something with the Monte Carlo simulations. It's probably not mathematically
     * correct, but shall take an average over all the simulated rate paths.
     */
    @Override
    public double avgExpectedReturnRate(long $task) {
        double result = 0.0;
        for(int i = 0; i < runs; i++)
            result += results.get(i);
        result /= runs;
        return result;
    }
    
    private class MonteCarloTask implements Callable<List<Double>> {
        
        private final SliceRange range;
        
        public MonteCarloTask(SliceRange sliceRange) {
            this.range = sliceRange;
        }
        
        public List<Double> call() {
            List<Double> localResults = new ArrayList<>();
            for(int i = range.begin; i < range.end; i += range.step) {
                MonteCarloPath mcPath = new MonteCarloPath(returns, steps);
                mcPath.computeFluctuations(seeds[i]);
                mcPath.computePathValues(PATH_START_VALUE);
                localResults.add(new Returns(mcPath).expectedReturnRate);
            }
            return localResults;
        }
    }
}
