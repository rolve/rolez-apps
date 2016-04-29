package ch.trick17.rolezapps.quicksort;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rolez.lang.GuardedArray;

public class QuicksortTest {
    
    @Test
    public void testSort() {
        // Only tests the single-threaded sort implementation
        Quicksort quicksort = new Quicksort(0);
        
        int n = 1000;
        GuardedArray<int[]> ints = quicksort.shuffledInts(n);
        quicksort.sort(ints);
        for(int i = 0; i < n; i++)
            assertEquals(i, ints.data[i]);
    }
}
