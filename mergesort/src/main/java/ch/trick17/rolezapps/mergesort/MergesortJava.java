package ch.trick17.rolezapps.mergesort;

import static rolez.lang.GuardedArray.wrap;

import rolez.lang.GuardedArray;
import rolez.util.Random;

public class MergesortJava extends Mergesort {
    
    public MergesortJava(int maxLevel, long $task) {
        super(maxLevel, $task);
    }
    
    @Override
    public GuardedArray<int[]> shuffledInts(int n, long $task) {
        int[] array = new int[n];
        for(int i = 0; i < n; i += 1)
            array[i] = i;
        Random random = new Random();
        for(int i = n - 1; i > 0; i -= 1) {
            int index = random.nextInt(i + 1);
            int t = array[index];
            array[index] = array[i];
            array[i] = t;
        }
        return wrap(array);
    }
    
    @Override
    public void sort(GuardedArray<int[]> a, long $task) {
        int[] b = a.data.clone();
        doSort(b, a.data, 0, a.data.length, 0);
    }
    
    private void doSort(int[] b, int[] a, int begin, int end, int level) {
        if((end - begin) < 2)
            return;
        
        int middle = (begin + end) / 2;
        Thread child = null;
        if(level < this.maxLevel) {
            child = new Thread($doSortTask(a, b, begin, middle, level + 1));
            child.start();
            doSort(a, b, middle, end, level + 1);
        }
        else {
            doSort(a, b, begin, middle, level + 1);
            doSort(a, b, middle, end, level + 1);
        }
        
        while(child != null)
            try {
                child.join();
                child = null;
            } catch(InterruptedException e) {}
        merge(b, a, begin, middle, end);
    }
    
    private Runnable $doSortTask(final int[] b, final int[] a, final int begin, final int end,
            final int level) {
        return new Runnable() {
            public void run() {
                doSort(b, a, begin, end, level);
            }
        };
    }
    
    private static void merge(int[] b, int[] a, int begin, int middle, int end) {
        int i = begin;
        int j = middle;
        for(int k = begin; k < end; k += 1) {
            if(i < middle && (j >= end || b[i] <= b[j])) {
                a[k] = b[i];
                i += 1;
            }
            else {
                a[k] = b[j];
                j += 1;
            }
        }
    }
}
