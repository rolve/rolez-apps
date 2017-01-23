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

package ch.trick17.rolezapps.montecarlojava;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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
public class MonteCarloApp {
    
    private static final double pathStartValue = 100.0;
    
    private final int runs;
    private final int numTasks;
    
    private final ReturnPath returnPath;
    private final int timeSteps;
    
    private final List<Long> seeds = new ArrayList<>();
    private final List<Double> results = new ArrayList<>();

    
    public MonteCarloApp(File ratesFile, int timeSteps, int runs, int numTasks) {
        this.timeSteps = timeSteps;
        this.runs = runs;
        this.numTasks = numTasks;
        
        // Measure the requested path rate.
        RatePath ratePath = RatePath.readRatesFile(ratesFile);
        
        // Now prepare for MC runs.
        returnPath = new ReturnPath(ratePath);
        
        // Now create the seeds for the tasks.
        for(int i = 0; i < runs; i++)
            seeds.add((long) i * 11);
    }
    
    public void run() {
        SliceRange fullRange = new SliceRange(0, runs, 1);
        SliceRange[] ranges = ContiguousPartitioner.INSTANCE.partition(fullRange, numTasks).data;
        
        List<FutureTask<List<Double>>> tasks = new ArrayList<FutureTask<List<Double>>>();
        for(int i = 1; i < numTasks; i++) {
            FutureTask<List<Double>> task = new FutureTask<List<Double>>(
                    new MonteCarloTask(ranges[i], seeds, returnPath, timeSteps));
            new Thread(task).start();
            tasks.add(task);
        }
        List<Double> ownResults = new MonteCarloTask(ranges[0], seeds, returnPath, timeSteps)
                .call();
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
    public double avgExpectedReturnRate() {
        double result = 0.0;
        for(int i = 0; i < runs; i++)
            result += results.get(i);
        
        result /= runs;
        return result;
    }
    
    private static class MonteCarloTask implements Callable<List<Double>> {
        
        private final SliceRange range;
        private final List<Long> seeds;
        private final ReturnPath returnPath;
        private final int timeSteps;
        
        public MonteCarloTask(SliceRange sliceRange, List<Long> seeds, ReturnPath returnPath,
                int timeSteps) {
            this.range = sliceRange;
            this.seeds = seeds;
            this.returnPath = returnPath;
            this.timeSteps = timeSteps;
        }
        
        public List<Double> call() {
            List<Double> results = new ArrayList<>();
            for(int i = range.begin; i < range.end; i += range.step) {
                MonteCarloPath mcPath = new MonteCarloPath(returnPath, timeSteps);
                mcPath.computeFluctuationsGaussian(seeds.get(i));
                mcPath.computePathValue(pathStartValue);
                RatePath rateP = new RatePath(mcPath.name, mcPath.startDate, mcPath.endDate,
                        mcPath.dTime, mcPath.getPathValues());
                results.add(new ReturnPath(rateP).expectedReturnRate);
            }
            return results;
        }
    }
}
