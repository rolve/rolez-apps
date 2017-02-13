package ch.trick17.rolezapps.mergesort;

import rolez.lang.GuardedSlice;

public class MergesortLocalOpt extends Mergesort {
    
    public MergesortLocalOpt(int maxLevel, long $task) {
        super(maxLevel, $task);
    }
    
    @Override
    public void merge(GuardedSlice<int[]> b, GuardedSlice<int[]> a, int begin, int middle, int end,
            long $task) {
        int i = begin;
        int j = middle;
        guardReadOnly(b, $task);
        guardReadWrite(a, $task);
        for(int k = begin; k < end; k += 1) {
            if(i < middle && (j >= end || b.getInt(i) <= b.getInt(j))) {
                a.setInt(k, b.getInt(i));
                i += 1;
            }
            else {
                a.setInt(k, b.getInt(j));
                j += 1;
            }
        }
    }
}
