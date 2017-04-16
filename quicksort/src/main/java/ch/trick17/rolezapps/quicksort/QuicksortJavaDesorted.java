package ch.trick17.rolezapps.quicksort;

import static rolez.lang.GuardedArray.wrap;

import java.util.Random;

import rolez.lang.GuardedArray;

/**
 * Shuffles the data in a way that causes the maximum number of swaps, i.e., it does the reverse of
 * quicksort, but unconditionally. This is different from just a reversed sorted array, as such an
 * array is sorted after one level of swapping.
 * <p>
 * Interestingly, quicksort performs just as well (or even slightly better) on this data set as on
 * already sorted data (at least on my system). Compared to a random shuffling of the data, both
 * this and the sorted data take around 5x less time (even using the perfect pivot for all data
 * sets). The reason is likely perfect vs. poor branch prediction!
 * 
 * @author Michael Faes
 */
public class QuicksortJavaDesorted extends QuicksortJavaPerfectPivot {
    
    public QuicksortJavaDesorted(int maxLevel, long $task) {
        super(maxLevel, $task);
    }
    
    @Override
    public GuardedArray<int[]> shuffledInts(int n, Random random, long $task) {
        int[] array = new int[n];
        for(int i = 0; i < n; i += 1)
            array[i] = i;
            
        desort(array, 0, n);
        return wrap(array);
    }
    
    private void desort(int[] s, int begin, int end) {
        int left = begin;
        int right = end - 1;
        while(left <= right) {
            final int temp = s[left];
            s[left] = s[right];
            s[right] = temp;
            left += 1;
            right -= 1;
        }
        
        if(begin < right)
            desort(s, begin, right + 1);
        if(left < (end - 1))
            desort(s, left, end);
    }
}
