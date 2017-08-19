package ch.trick17.rolezapps.mergesort;

import rolez.lang.GuardedArray;
import rolez.lang.GuardedSlice;
import rolez.lang.TaskSystem;

public class MergesortRolezL extends MergesortRolez {
    
    public MergesortRolezL(long $task) {
        super($task);
    }
    
    public MergesortRolezL(int maxLevel, long $task) {
        super(maxLevel, $task);
    }
    
    public static void main(String[] args) {
        TaskSystem.getDefault().run(new MergesortRolezL(0L).main$Task(GuardedArray.<String[]> wrap(args)));
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
