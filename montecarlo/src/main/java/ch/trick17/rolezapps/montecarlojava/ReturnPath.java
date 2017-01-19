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

import static java.lang.Double.isNaN;

/**
 * Class for representing the returns of a given security.
 * <p>
 * To do list:
 * <ol>
 * <li>Define a window over which the mean drift and volatility are calculated.</li>
 * <li>Hash table to reference {DATE}->{pathValue-index}.</li>
 * </ol>
 *
 * @author H W Yau
 * @version $Revision: 1.21 $ $Date: 1999/02/16 18:52:41 $
 */
public class ReturnPath extends Path {
    
    /**
     * An instance variable, for storing the return values.
     */
    private final double[] pathValue;
    /**
     * Value for the expected return rate.
     */
    private double expectedReturnRate = Double.NaN;
    /**
     * Value for the volatility, calculated from the return data.
     */
    private double volatility = Double.NaN;
    /**
     * Value for the volatility-squared, a more natural quantity to use for many
     * of the calculations.
     */
    private double volatility2 = Double.NaN;
    /**
     * Value for the mean of this return.
     */
    private double mean = Double.NaN;
    /**
     * Value for the variance of this return.
     */
    private double variance = Double.NaN;
    
    /**
     * @param name
     *            name of the path
     * @param startDate
     *            start date
     * @param endDate
     *            end date
     * @param dTime
     *            dTime
     * @param pathValue
     *            for creating a return path with a precomputed path value.
     *            Indexed from 1 to <code>nPathArray-1</code>.
     */
    public ReturnPath(String name, int startDate, int endDate, double dTime, double[] pathValue) {
        super(name, startDate, endDate, dTime);
        this.pathValue = pathValue;
    }
    
    /**
     * Accessor method for private instance variable
     * <code>expectedReturnRate</code>.
     *
     * @return Value of instance variable <code>expectedReturnRate</code>.
     */
    public double getExpectedReturnRate() {
        if(isNaN(expectedReturnRate))
            throw new AssertionError("Variable expectedReturnRate is undefined!");
        return(expectedReturnRate);
    }
    
    /**
     * Accessor method for private instance variable <code>volatility</code>.
     *
     * @return Value of instance variable <code>volatility</code>.
     */
    public double getVolatility() {
        if(isNaN(volatility))
            throw new AssertionError("Variable volatility is undefined!");
        return(volatility);
    }
    
    /**
     * A single method for invoking all the necessary methods which estimate the
     * parameters.
     */
    public void estimatePath() {
        computeMean();
        computeVariance();
        computeExpectedReturnRate();
        computeVolatility();
    }
    
    /**
     * Method to calculate the expected return rate from the return data, using
     * the relationship: \mu = \frac{\bar{u}}{\Delta t} + \frac{\sigma^2}{2}
     */
    private void computeExpectedReturnRate() {
        expectedReturnRate = mean / (dTime) + 0.5 * volatility2;
    }
    
    /**
     * Method to calculate <code>volatility</code> and <code>volatility2</code>
     * from the return path data, using the relationship, based on the
     * precomputed <code>variance</code>. \sigma^2 = s^2\Delta t
     */
    private void computeVolatility() {
        volatility2 = variance / (dTime);
        volatility = Math.sqrt(volatility2);
    }
    
    /**
     * Method to calculate the mean of the return, for use by other
     * calculations.
     */
    private void computeMean() {
        mean = 0.0;
        for(int i = 1; i < pathValue.length; i++)
            mean += pathValue[i];
        mean /= (pathValue.length - 1.0);
    }
    
    /**
     * Method to calculate the variance of the retrun, for use by other
     * calculations.
     */
    private void computeVariance() {
        variance = 0.0;
        for(int i = 1; i < pathValue.length; i++)
            variance += (pathValue[i] - mean) * (pathValue[i] - mean);
        variance /= (pathValue.length - 1.0);
    }
}
