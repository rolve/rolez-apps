package ch.trick17.rolezapps.quicksort;

import static rolez.lang.GuardedArray.wrap;

import rolez.lang.GuardedArray;
import rolez.lang.Task;

public class QuicksortJavaSorted extends QuicksortJavaPerfectPivot {
    
    public QuicksortJavaSorted(int maxLevel) {
        super(maxLevel);
    }
    
    @Override
    public GuardedArray<int[]> shuffledInts(int n, Task<?> $task) {
        int[] array = new int[n];
        for(int i = 0; i < n; i += 1)
            array[i] = i;
            
        return wrap(array);
    }
}
