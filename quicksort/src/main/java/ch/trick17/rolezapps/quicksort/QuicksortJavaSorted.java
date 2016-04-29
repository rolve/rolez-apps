package ch.trick17.rolezapps.quicksort;

import rolez.lang.GuardedArray;

public class QuicksortJavaSorted extends QuicksortJavaPerfectPivot {
    
    public QuicksortJavaSorted(final int maxLevel) {
        super(maxLevel);
    }
    
    @Override
    public GuardedArray<int[]> shuffledInts(final int n) {
        int[] array = new int[n];
        for(int i = 0; i < n; i += 1)
            array[i] = i;
            
        return GuardedArray.wrap(array);
    }
}
