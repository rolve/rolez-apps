package ch.trick17.rolezapps.quicksort;

import java.util.Random;

import rolez.lang.GuardedArray;

public class QuicksortJava extends Quicksort {
    
    public QuicksortJava(final int maxLevel) {
        super(maxLevel);
    }
    
    @Override
    public GuardedArray<int[]> shuffledInts(final int n) {
        int[] array = new int[n];
        for(int i = 0; i < n; i += 1)
            array[i] = i;
        final Random random = new Random();
        for(int i = n - 1; i > 0; i -= 1) {
            final int index = random.nextInt(i + 1);
            final int t = array[index];
            array[index] = array[i];
            array[i] = t;
        }
        return GuardedArray.wrap(array);
    }
    
    @Override
    public void sort(final GuardedArray<int[]> s) {
        this.doSort(s.data, 0, s.data.length, 0);
    }
    
    public void doSort(final int[] s, final int begin, final int end, final int level) {
        int pivot = this.pivot(s, begin, end);
        int left = begin;
        int right = end - 1;
        while(left <= right) {
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
                child = new Thread(this.$doSortTask(s, begin, right + 1, level + 1));
                child.start();
            }
            if(sortRight)
                this.doSort(s, left, end, level + 1);
        }
        else {
            if(sortLeft)
                this.doSort(s, begin, right + 1, level + 1);
            if(sortRight)
                this.doSort(s, left, end, level + 1);
        }
        
        while(child != null)
            try {
                child.join();
                child = null;
            } catch(InterruptedException e) {}
    }
    
    public Runnable $doSortTask(final int[] s, final int begin, final int end, final int level) {
        return new Runnable() {
            public void run() {
                doSort(s, begin, end, level);
            }
        };
    }
    
    public int pivot(final int[] s, final int begin, final int end) {
        final int l = s[begin];
        final int m = s[begin + ((end - begin) / 2)];
        final int r = s[end - 1];
        if(l < m) {
            if(m < r)
                return m;
            else if(l < r)
                return l;
            else
                return r;
        }
        else {
            if(l < r)
                return l;
            else if(m < r)
                return r;
            else
                return m;
        }
    }
}
