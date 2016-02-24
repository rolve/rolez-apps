package ch.trick17.rolezapps.quicksort;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QuicksortTest {
    
    private final Quicksort quicksort = new Quicksort();
    
    @Test
    public void testLog2() {
        assertEquals(0, quicksort.log2(1));
        assertEquals(1, quicksort.log2(2));
        assertEquals(2, quicksort.log2(4));
        assertEquals(4, quicksort.log2(16));
        
        assertEquals(1, quicksort.log2(3));
        assertEquals(4, quicksort.log2(31));
    }
}
