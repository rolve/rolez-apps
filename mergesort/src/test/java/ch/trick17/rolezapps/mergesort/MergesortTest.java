package ch.trick17.rolezapps.mergesort;

import static org.junit.Assert.assertEquals;
import static rolez.lang.Task.currentTask;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rolez.lang.GuardedArray;
import rolez.lang.Task;

public class MergesortTest {

    @BeforeClass
    public static void setup() {
        Task.registerNewRootTask();
    }
    
    @AfterClass
    public static void tearDown() {
        Task.unregisterRootTask();
    }
    
    @Test
    public void testSort() {
        // Only tests the single-threaded sort implementation. Since nothing is ever passed around,
        // guarding is never initialized, and the $task parameter is never really used
        Mergesort mergesort = new MergesortRolez(0, 0L);
        Random random = new Random();
        
        int n = 1000;
        GuardedArray<int[]> ints = mergesort.shuffledInts(n, random, 0L);
        mergesort.sort(ints, currentTask().idBits());
        for(int i = 0; i < n; i++)
            assertEquals(i, ints.data[i]);
    }
}
