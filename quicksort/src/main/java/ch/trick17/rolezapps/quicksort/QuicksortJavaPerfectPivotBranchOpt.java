package ch.trick17.rolezapps.quicksort;

import java.util.Scanner;

import rolez.lang.GuardedArray;

/**
 * Apparently, the "naive" quicksort implementation has poor branch prediction performance. This
 * implementation improves this a bit.
 * 
 * @author Michael Faes
 */
public class QuicksortJavaPerfectPivotBranchOpt extends QuicksortJava {
    
    public static void main(String[] args) {
        QuicksortJava quicksort = new QuicksortJavaPerfectPivotBranchOpt(0);
        int n = 50000000;
        GuardedArray<int[]> ints;
        
        System.out.println("Warming up...");
        for(int i = 0; i < 2; i++) {
            ints = quicksort.shuffledInts(n, null);
            quicksort.sort(ints, null);
        }
        ints = quicksort.shuffledInts(n, null);
        
        System.out.println("Enter to start");
        new Scanner(System.in).nextLine();
        quicksort.sort(ints, null);
    }
    
    public QuicksortJavaPerfectPivotBranchOpt(int maxLevel) {
        super(maxLevel);
    }
    
    @Override
    public void doSort(int[] s, int begin, int end, int level) {
        int pivot = this.pivot(s, begin, end);
        int left = begin;
        int right = end - 1;
        
        while(left <= right) {
            // Branchless increment
            left += s[left] < pivot ? 1 : 0;
            left += s[left] < pivot ? 1 : 0;
            right -= s[right] > pivot ? 1 : 0;
            right -= s[right] > pivot ? 1 : 0;
            while(s[left] < pivot)
                left += 1;
            while(s[right] > pivot)
                right -= 1;
                
            if(left <= right) {
                final int temp = s[left];
                s[left] = s[right];
                s[right] = temp;
                left += 1;
                right -= 1;
            }
        }
        boolean sortLeft = begin < right;
        boolean sortRight = left < (end - 1);
        
        Thread child = null;
        if(level < maxLevel) {
            if(sortLeft) {
                child = new Thread($doSortTask(s, begin, right + 1, level + 1));
                child.start();
            }
            if(sortRight)
                doSort(s, left, end, level + 1);
        }
        else {
            if(sortLeft)
                doSort(s, begin, right + 1, level + 1);
            if(sortRight)
                doSort(s, left, end, level + 1);
        }
        
        while(child != null)
            try {
                child.join();
                child = null;
            } catch(InterruptedException e) {}
    }
    
    @Override
    public int pivot(int[] s, int begin, int end) {
        return begin + (end - begin) / 2;
    }
}
