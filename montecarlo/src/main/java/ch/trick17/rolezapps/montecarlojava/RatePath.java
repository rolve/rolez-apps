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

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for recording the values in the time-dependent path of a security.
 * <p>
 * To Do list:
 * <ol>
 * <li><i>None!</i>
 * </ol>
 *
 * @author H W Yau
 * @version $Revision: 1.28 $ $Date: 1999/02/16 18:52:29 $
 */
public class RatePath extends Path {
    
    /**
     * Class variable for determining which field in the stock data should be
     * used. This is currently set to point to the 'closing price'.
     */
    public static final int DATUM_FIELD = 4;
    /**
     * Class variable to represent the minimal date, whence the stock prices
     * appear. Used to trap any potential problems with the data.
     */
    public static final int MIN_DATE = 19000101;
    /**
     * Class variable for defining what is meant by a small number, small enough
     * to cause an arithmetic overflow when dividing. According to the Java
     * Nutshell book, the actual range is +/-4.9406564841246544E-324
     */
    public static final double EPSILON = 10.0 * Double.MIN_VALUE;
    
    /**
     * An instance variable, for storing the rate's path values itself.
     */
    public final double[] pathValues;
    
    public RatePath(String name, int startDate, int endDate, double dTime, double[] pathValues) {
        super(name, startDate, endDate, dTime);
        this.pathValues = pathValues;
    }
    
    /**
     * Method for reading in data file, in a given format. Namely:
     * 
     * <pre>
     *       881003,0.0000,14.1944,13.9444,14.0832,2200050,0
     *       881004,0.0000,14.1668,14.0556,14.1668,1490850,0
     *       ...
     *       990108,35.8125,36.7500,35.5625,35.8125,4381200,0
     *       990111,35.8125,35.8750,34.8750,35.1250,3920800,0
     *       990112,34.8750,34.8750,34.0000,34.0625,3577500,0
     * </pre>
     * <p>
     * Where the fields represent, one believes, the following:
     * <ol>
     * <li>The date in 'YYMMDD' format</li>
     * <li>Open</li>
     * <li>High</li>
     * <li>Low</li>
     * <li>Last</li>
     * <li>Volume</li>
     * <li>Open Interest</li>
     * </ol>
     * One will probably make use of the closing price, but this can be
     * redefined via the class variable <code>DATUMFIELD</code>. Note that since
     * the read in data are then used to compute the return, this would be a
     * good place to trap for zero values in the data, which will cause all
     * sorts of problems.
     *
     * @param file
     *            the data file
     * @return A rate path with the read data
     */
    public static RatePath readRatesFile(File file) {
        // Read all the lines of data into a list.        
        List<String> lines = new ArrayList<String>(100);
        try(BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = in.readLine()) != null)
                lines.add(line);
        } catch(final IOException e) {
            throw new RuntimeException("Problem reading data from the file", e);
        }
        
        // Now create an array to store the rates data.
        double[] pathValues = new double[lines.size()];
        int[] pathDates = new int[lines.size()];
        
        for(int i = 0; i < lines.size(); i++) {
            String[] fields = lines.get(i).split(",");
            int date = parseInt("19" + fields[0]);
            
            double value = parseDouble(fields[DATUM_FIELD]);
            if(date <= MIN_DATE || abs(value) < EPSILON)
                throw new AssertionError("erroneous data in " + file + " indexed by date="
                        + fields[0] + ".");
            
            pathDates[i] = date;
            pathValues[i] = value;
        }
        
        return new RatePath(file.getName(), pathDates[0], pathDates[lines.size() - 1], 1.0 / 365.0,
                pathValues);
    }
}
