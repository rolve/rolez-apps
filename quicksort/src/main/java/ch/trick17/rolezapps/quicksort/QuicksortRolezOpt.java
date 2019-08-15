package ch.trick17.rolezapps.quicksort;

import static rolez.lang.Guarded.*;

public class QuicksortRolezOpt extends QuicksortRolez {
    
    public QuicksortRolezOpt(final int maxLevel, final long $task) {
        super(maxLevel, $task);
    }
    
    public void doSort$Unguarded(final rolez.lang.GuardedSlice<int[]> s, final int begin, final int end, final int level, final long $task) {
        final int pivot = this.pivot$Unguarded(s, begin, end, $task);
        int left = begin;
        int right = end - 1;
        while(left <= right) {
            while(s.getInt(left) < pivot)
                left++;
            while(s.getInt(right) > pivot)
                right--;
            if(left <= right) {
                final int temp = s.getInt(left);
                s.setInt(left, s.getInt(right));
                s.setInt(right, temp);
                left++;
                right--;
            }
        }
        final boolean sortLeft = begin < right;
        final boolean sortRight = left < (end - 1);
        if(level < this.maxLevel) {
            if(sortLeft && sortRight) {
                { /* parallel-and */
                    final ch.trick17.rolezapps.quicksort.QuicksortRolezOpt $t1Arg0 = this;
                    final rolez.lang.GuardedSlice<int[]> $t1Arg1 = s.slice(begin, right + 1);
                    final int $t1Arg2 = begin;
                    final int $t1Arg3 = right + 1;
                    final int $t1Arg4 = level + 1;
                    final ch.trick17.rolezapps.quicksort.QuicksortRolezOpt $t2Arg0 = this;
                    final rolez.lang.GuardedSlice<int[]> $t2Arg1 = s.slice(left, end);
                    final int $t2Arg2 = left;
                    final int $t2Arg3 = end;
                    final int $t2Arg4 = level + 1;
                    
                    final java.lang.Object[] $t1Passed = {$t1Arg1};
                    final java.lang.Object[] $t1Shared = {};
                    final java.lang.Object[] $t2Passed = {$t2Arg1};
                    final java.lang.Object[] $t2Shared = {};
                    
                    final rolez.lang.Task<?> $t1 = new rolez.lang.Task<java.lang.Void>($t1Passed, $t1Shared, true) {
                        @java.lang.Override
                        protected java.lang.Void runRolez() {
                            $t1Arg0.doSort$Unguarded($t1Arg1, $t1Arg2, $t1Arg3, $t1Arg4, idBits());
                            return null;
                        }
                    };
                    final rolez.lang.Task<?> $t2 = new rolez.lang.Task<java.lang.Void>($t2Passed, $t2Shared, $t1.idBits(), true) {
                        @java.lang.Override
                        protected java.lang.Void runRolez() {
                            $t2Arg0.doSort$Unguarded($t2Arg1, $t2Arg2, $t2Arg3, $t2Arg4, idBits());
                            return null;
                        }
                    };
                    
                    try {
                        rolez.lang.TaskSystem.getDefault().start($t1);
                        rolez.lang.TaskSystem.getDefault().run($t2);
                    } finally {
                        $t1.get();
                    }
                }
            }
            else
                if(sortLeft) {
                this.doSort$Unguarded(s.slice(begin, right + 1), begin, right + 1, level + 1, $task);
            }
            else
                if(sortRight) {
                this.doSort$Unguarded(s.slice(left, end), left, end, level + 1, $task);
            }
        }
        else {
            if(sortLeft)
                this.doSort$Unguarded(s, begin, right + 1, level + 1, $task);
            
            if(sortRight)
                this.doSort$Unguarded(s, left, end, level + 1, $task);
        }
    }
}
