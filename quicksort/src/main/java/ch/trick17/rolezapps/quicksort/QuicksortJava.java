package ch.trick17.rolezapps.quicksort;

import rolez.lang.GuardedArray;

public class QuicksortJava extends Quicksort {
    
    public QuicksortJava(final int maxLevel, long $task) {
        super(maxLevel, $task);
    }
    
    @Override
    public void sort$Unguarded(final GuardedArray<int[]> s, long $task) {
        doSort(s.data, 0, s.data.length, 0);
    }
    
    private void doSort(int[] s, int begin, int end, int level) {
        int pivot = pivot(s, begin, end);
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
                child = new Thread($doSortTask(s, begin, right + 1, level + 1));
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
    
    private Runnable $doSortTask(final int[] s, final int begin, final int end, final int level) {
        return new Runnable() {
            public void run() {
                doSort(s, begin, end, level);
            }
        };
    }
    
    private int pivot(int[] s, int begin, int end) {
        int l = s[begin];
        int m = s[begin + ((end - begin) / 2)];
        int r = s[end - 1];
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
