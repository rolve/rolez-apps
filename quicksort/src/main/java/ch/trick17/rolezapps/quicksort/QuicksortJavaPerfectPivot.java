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
        QuicksortJava quicksort = new QuicksortJavaPerfectPivot(0, 0L);
        int n = 50000000;
        GuardedArray<int[]> ints;
        
        System.out.println("Warming up...");
        for(int i = 0; i < 2; i++) {
            ints = quicksort.shuffledInts(n, 0L);
            quicksort.sort(ints, 0L);
        }
        ints = quicksort.shuffledInts(n, 0L);
        
        System.out.println("Enter to start");
        new Scanner(System.in).nextLine();
        quicksort.sort(ints, 0L);
    }
    
    public QuicksortJavaPerfectPivot(int maxLevel, long $task) {
        super(maxLevel, $task);
    }
    
    @Override
    public int pivot(int[] s, int begin, int end) {
        return (begin + end) / 2;
    }
}
