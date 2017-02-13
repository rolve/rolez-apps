package ch.trick17.rolezapps.quicksort;

import static rolez.lang.GuardedArray.wrap;

import rolez.lang.GuardedArray;

public class QuicksortJavaSorted extends QuicksortJavaPerfectPivot {
    
    public QuicksortJavaSorted(int maxLevel, long $task) {
        super(maxLevel, $task);
    }
    
    @Override
    public GuardedArray<int[]> shuffledInts(int n, long $task) {
        int[] array = new int[n];
        for(int i = 0; i < n; i += 1)
            array[i] = i;
            
        return wrap(array);
    }
}
