package ch.trick17.rolezapps.quicksort;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rolez.lang.GuardedArray;

public class QuicksortTest {
    
    @Test
    public void testSort() {
        // Only tests the single-threaded sort implementation. Since nothing is ever passed around,
        // guarding is never initialized, and the $task parameter is never really used
        Quicksort quicksort = new Quicksort(0, null);
        
        int n = 1000;
        GuardedArray<int[]> ints = quicksort.shuffledInts(n, null);
        quicksort.sort(ints, null);
        for(int i = 0; i < n; i++)
            assertEquals(i, ints.data[i]);
    }
}
