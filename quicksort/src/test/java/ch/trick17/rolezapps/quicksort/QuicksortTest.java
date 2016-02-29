package ch.trick17.rolezapps.quicksort;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rolez.lang.GuardedArray;

public class QuicksortTest {
    
    @Test
    public void testLog2() {
        Quicksort quicksort = new Quicksort();
        
        assertEquals(0, quicksort.log2(1));
        assertEquals(1, quicksort.log2(2));
        assertEquals(2, quicksort.log2(4));
        assertEquals(4, quicksort.log2(16));
        
        assertEquals(1, quicksort.log2(3));
        assertEquals(4, quicksort.log2(31));
    }
    
    @Test
    public void testSort() {
        // Only tests the single-threaded sort implementation
        Quicksort quicksort = new Quicksort(1);
        
        int n = 1000;
        GuardedArray<int[]> ints = quicksort.shuffledInts(n);
        quicksort.sort(ints);
        for(int i = 0; i < n; i++)
            assertEquals(i, ints.data[i]);
    }
}
