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

import static java.lang.Math.log;

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
public class Returns extends Path {
    
    public final double volatility;
    public final double expectedReturnRate;
    
    public Returns(RatePath ratePath) {
        super(ratePath.name, ratePath.startDate, ratePath.endDate, ratePath.dTime);
        
        /* Calculate the returns on a given rate path, via the definition for the instantaneous
         * compounded return. u_i = \ln{\frac{S_i}{S_{i-1}}} */
        double[] returnPathValues = new double[ratePath.pathValues.length];
        returnPathValues[0] = 0.0;
        for(int i = 1; i < ratePath.pathValues.length; i++)
            returnPathValues[i] = log(ratePath.pathValues[i] / ratePath.pathValues[i - 1]);
        
        double mean = 0.0;
        for(int i = 1; i < returnPathValues.length; i++)
            mean += returnPathValues[i];
        mean /= returnPathValues.length - 1.0;
        
        double variance = 0.0;
        for(int i = 1; i < returnPathValues.length; i++)
            variance += (returnPathValues[i] - mean) * (returnPathValues[i] - mean);
        variance /= returnPathValues.length - 1.0;
        
        double volatility2 = variance / dTime;
        volatility = Math.sqrt(volatility2);
        expectedReturnRate = mean / dTime + 0.5 * volatility2;
    }
}
