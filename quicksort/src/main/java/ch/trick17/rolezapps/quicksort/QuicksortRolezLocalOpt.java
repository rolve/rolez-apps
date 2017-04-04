package ch.trick17.rolezapps.quicksort;

import rolez.lang.GuardedSlice;
import rolez.lang.Task;
import rolez.lang.TaskSystem;

public class QuicksortRolezLocalOpt extends QuicksortRolez {
    
    public QuicksortRolezLocalOpt(final int maxLevel, long $task) {
        super(maxLevel, $task);
    }
    
    @Override
    public void doSort(GuardedSlice<int[]> s, int begin, int end, int level, long $task) {
        final int pivot = this.pivot(s, begin, end, $task);
        int left = begin;
        int right = end - 1;
        guardReadWrite(s, $task);
        while(left <= right) {
            while(s.getInt(left) < pivot)
                left += 1;
            while(s.getInt(right) > pivot)
                right -= 1;
            if(left <= right) {
                final int temp = s.getInt(left);
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
                TaskSystem.getDefault().start($doSortTask(s.slice(begin, right + 1, 1), begin, right
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
    
    @Override
    public Task<Void> $doSortTask(final GuardedSlice<int[]> s, final int begin, final int end,
            final int level) {
        return new Task<Void>(new Object[]{s}, new Object[]{}) {
            @Override
            protected Void runRolez() {
                long $task = idBits();
                int pivot = pivot(s, begin, end, $task);
                int left = begin;
                int right = end - 1;
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
                boolean sortLeft = begin < right;
                boolean sortRight = left < (end - 1);
                if(level < maxLevel) {
                    if(sortLeft)
                        TaskSystem.getDefault().start($doSortTask(s.slice(begin, right + 1, 1),
                                begin, right + 1, level + 1));
                    
                    if(sortRight)
                        doSort(s.slice(left, end, 1), left, end, level + 1, $task);
                }
                else {
                    if(sortLeft)
                        doSort(s, begin, right + 1, level + 1, $task);
                    
                    if(sortRight)
                        doSort(s, left, end, level + 1, $task);
                }
                return null;
            }
        };
    }
    
    @Override
    public int pivot(final GuardedSlice<int[]> s, int begin, int end, long $task) {
        guardReadOnly(s, $task);
        int l = s.getInt(begin);
        int m = s.getInt(begin + ((end - begin) / 2));
        int r = s.getInt(end - 1);
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
