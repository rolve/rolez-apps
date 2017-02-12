package ch.trick17.rolezapps.quicksort;

import java.util.Scanner;

import rolez.lang.GuardedArray;

/**
 * Always uses the perfect pivot (i.e., the median) by simply computing the mean of the begin and
 * end indices of the partition. This requires an array of ints where for each index <em>i</em>
 * there is exactly one item of value <em>i</em> in the array. The order is irrelevant.
 * 
 * @author Michael Faes
 */
public class QuicksortJavaPerfectPivot extends QuicksortJava {
    
    public static void main(String[] args) {
        QuicksortJava quicksort = new QuicksortJavaPerfectPivot(0);
        int n = 50000000;
        GuardedArray<int[]> ints;
        
        System.out.println("Warming up...");
        for(int i = 0; i < 2; i++) {
            ints = quicksort.shuffledInts(n, null);
            quicksort.sort(ints, null);
        }
        ints = quicksort.shuffledInts(n, null);
        
        System.out.println("Enter to start");
        new Scanner(System.in).nextLine();
        quicksort.sort(ints, null);
    }
    
    public QuicksortJavaPerfectPivot(int maxLevel) {
        super(maxLevel);
    }
    
    @Override
    public int pivot(int[] s, int begin, int end) {
        return (begin + end) / 2;
    }
}
