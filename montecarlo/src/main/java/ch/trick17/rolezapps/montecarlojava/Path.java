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
 * Base class for all the security objects, namely in terms of providing a consistent means of
 * identifying each such object.
 *
 * @author H W Yau
 * @version $Revision: 1.13 $ $Date: 1999/02/16 18:51:58 $
 */
public class Path {
    
    /**
     * Simple string name.
     */
    public final String name;
    /**
     * The start date for the path, in YYYYMMDD format.
     */
    public final int startDate;
    /**
     * The end date for the path, in YYYYMMDD format.
     */
    public final int endDate;
    /**
     * The change in time between two successive data values.
     */
    public final double dTime;
    
    public Path(String name, int startDate, int endDate, double dTime) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dTime = dTime;
    }
}
