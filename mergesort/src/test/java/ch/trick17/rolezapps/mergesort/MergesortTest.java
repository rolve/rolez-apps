package ch.trick17.rolezapps.mergesort;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rolez.lang.GuardedArray;

public class MergesortTest {
    
    @Test
    public void testSort() {
        // Only tests the single-threaded sort implementation. Since nothing is ever passed around,
        // guarding is never initialized, and the $task parameter is never really used
        Mergesort mergesort = new Mergesort(0, 0L);
        
        int n = 1000;
        GuardedArray<int[]> ints = mergesort.shuffledInts(n, 0L);
        mergesort.sort(ints, 0L);
        for(int i = 0; i < n; i++)
            assertEquals(i, ints.data[i]);
    }
}
