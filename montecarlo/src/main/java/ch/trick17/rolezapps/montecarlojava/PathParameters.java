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

/**
 * Class for defining the initialisation data for all tasks.
 *
 * @author H W Yau
 * @version $Revision: 1.10 $ $Date: 1999/02/16 18:52:53 $
 */
public class PathParameters {
    
    public final String name;
    public final int startDate;
    public final int endDate;
    public final double dTime;
    public final double expectedReturnRate;
    public final double volatility;
    public final int timeSteps;
    
    /**
     * Another constructor, slightly easier to use by having slightly fewer
     * arguments. Makes use of the "ReturnPath" object to accomplish this.
     *
     * @param obj
     *            Object used to define the instance variables which should be
     *            carried over to this object.
     * @param timeSteps
     *            The number of time steps which the Monte Carlo generator
     *            should make.
     */
    public PathParameters(ReturnPath obj, int timeSteps) {
        this.name = obj.name;
        this.startDate = obj.startDate;
        this.endDate = obj.endDate;
        this.dTime = obj.dTime;
        this.expectedReturnRate = obj.getExpectedReturnRate();
        this.volatility = obj.getVolatility();
        this.timeSteps = timeSteps;
    }
}
