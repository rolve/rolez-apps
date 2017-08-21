package ch.trick17.rolezapps.quicksort;

import rolez.lang.GuardedSlice;
import rolez.lang.TaskSystem;

public class QuicksortRolezL extends QuicksortRolez {
    
    public QuicksortRolezL(final int maxLevel, long $task) {
        super(maxLevel, $task);
    }
    
    @Override
    public void doSort(GuardedSlice<int[]> s, int begin, int end, int level, long $task) {
        int pivot = this.pivot(s, begin, end, $task);
        int left = begin;
        int right = end - 1;
        guardReadWrite(s, $task);
        while(left <= right) {
            while(s.getInt(left) < pivot)
                left += 1;
            while(s.getInt(right) > pivot)
                right -= 1;
            if(left <= right) {
                int temp = s.getInt(left);
                s.setInt(left, s.getInt(right));
                s.setInt(right, temp);
                left += 1;
                right -= 1;
            }
        }
        final boolean sortLeft = begin < right;
        final boolean sortRight = left < (end - 1);
        if(level < this.maxLevel) {
            if(sortLeft)
                TaskSystem.getDefault().start(doSort$Task(s.slice(begin, right + 1, 1), begin, right
                        + 1, level + 1));
            
            if(sortRight)
                this.doSort(s.slice(left, end, 1), left, end, level + 1, $task);
        }
        else {
            if(sortLeft)
                this.doSort(s, begin, right + 1, level + 1, $task);
            
            if(sortRight)
                this.doSort(s, left, end, level + 1, $task);
        }
    }
}
