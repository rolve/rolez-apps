package ch.trick17.rolezapps.mergesort;

public class MergesortRolezOpt extends MergesortRolez {
    
    public MergesortRolezOpt(final int maxLevel, final long $task) {
        super(maxLevel, $task);
    }
    
    public void doSort$Unguarded(final rolez.lang.GuardedSlice<int[]> b, final rolez.lang.GuardedSlice<int[]> a, final int begin, final int end, final int level, final long $task) {
        if((end - begin) < 2)
            return;
        
        if((end - begin) == 2) {
            if(b.getInt(begin) > b.getInt(begin + 1)) {
                a.setInt(begin, b.getInt(begin + 1));
                a.setInt(begin + 1, b.getInt(begin));
            }
            
            return;
        }
        
        final int middle = (begin + end) / 2;
        if(level < this.maxLevel) {
            { /* parallel-and */
                final ch.trick17.rolezapps.mergesort.MergesortRolezOpt $t1Arg0 = this;
                final rolez.lang.GuardedSlice<int[]> $t1Arg1 = a.slice(begin, middle);
                final rolez.lang.GuardedSlice<int[]> $t1Arg2 = b.slice(begin, middle);
                final int $t1Arg3 = begin;
                final int $t1Arg4 = middle;
                final int $t1Arg5 = level + 1;
                final ch.trick17.rolezapps.mergesort.MergesortRolezOpt $t2Arg0 = this;
                final rolez.lang.GuardedSlice<int[]> $t2Arg1 = a.slice(middle, end);
                final rolez.lang.GuardedSlice<int[]> $t2Arg2 = b.slice(middle, end);
                final int $t2Arg3 = middle;
                final int $t2Arg4 = end;
                final int $t2Arg5 = level + 1;
                
                final java.lang.Object[] $t1Passed = {$t1Arg1, $t1Arg2};
                final java.lang.Object[] $t1Shared = {};
                final java.lang.Object[] $t2Passed = {$t2Arg1, $t2Arg2};
                final java.lang.Object[] $t2Shared = {};
                
                final rolez.lang.Task<?> $t1 = new rolez.lang.Task<java.lang.Void>($t1Passed, $t1Shared, true) {
                    @java.lang.Override
                    protected java.lang.Void runRolez() {
                        $t1Arg0.doSort$Unguarded($t1Arg1, $t1Arg2, $t1Arg3, $t1Arg4, $t1Arg5, idBits());
                        return null;
                    }
                };
                final rolez.lang.Task<?> $t2 = new rolez.lang.Task<java.lang.Void>($t2Passed, $t2Shared, $t1.idBits(), true) {
                    @java.lang.Override
                    protected java.lang.Void runRolez() {
                        $t2Arg0.doSort$Unguarded($t2Arg1, $t2Arg2, $t2Arg3, $t2Arg4, $t2Arg5, idBits());
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
        else {
            this.doSort$Unguarded(a, b, begin, middle, level + 1, $task);
            this.doSort$Unguarded(a, b, middle, end, level + 1, $task);
        }
        
        this.merge$Unguarded(b, a, begin, middle, end, $task);
    }
}
