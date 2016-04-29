package ch.trick17.rolezapps.quicksort;

public class QuicksortLocalOpt extends Quicksort {
    
    public QuicksortLocalOpt(final int maxLevel) {
        super(maxLevel);
    }
    
    @Override
    public void doSort(final rolez.lang.GuardedSlice<int[]> s, final int begin, final int end,
            final int level) {
        final int pivot = this.pivot(s, begin, end);
        int left = begin;
        int right = end - 1;
        guardReadWrite(s);
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
                rolez.lang.TaskSystem.getDefault().start(this.$doSortTask(s.slice(begin, right + 1,
                        1), begin, right + 1, level + 1));
                        
            if(sortRight)
                this.doSort(s.slice(left, end, 1), left, end, level + 1);
        }
        else {
            if(sortLeft)
                this.doSort(s, begin, right + 1, level + 1);
                
            if(sortRight)
                this.doSort(s, left, end, level + 1);
        }
    }
    
    @Override
    public java.util.concurrent.Callable<java.lang.Void> $doSortTask(
            final rolez.lang.GuardedSlice<int[]> s, final int begin, final int end,
            final int level) {
        s.pass();
        return new java.util.concurrent.Callable<java.lang.Void>() {
            public java.lang.Void call() {
                s.registerNewOwner();
                try {
                    final int pivot = QuicksortLocalOpt.this.pivot(s, begin, end);
                    int left = begin;
                    int right = end - 1;
                    guardReadWrite(s);
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
                    if(level < QuicksortLocalOpt.this.maxLevel) {
                        if(sortLeft)
                            rolez.lang.TaskSystem.getDefault().start(QuicksortLocalOpt.this
                                    .$doSortTask(s
                                            .slice(begin, right + 1, 1), begin, right + 1, level
                                                    + 1));
                                                    
                        if(sortRight)
                            QuicksortLocalOpt.this.doSort(s.slice(left, end, 1), left, end, level
                                    + 1);
                    }
                    else {
                        if(sortLeft)
                            QuicksortLocalOpt.this.doSort(s, begin, right + 1, level + 1);
                            
                        if(sortRight)
                            QuicksortLocalOpt.this.doSort(s, left, end, level + 1);
                    }
                } finally {
                    s.releasePassed();
                }
                return null;
            }
        };
    }
    
    @Override
    public int pivot(final rolez.lang.GuardedSlice<int[]> s, final int begin, final int end) {
        guardReadOnly(s);
        final int l = s.getInt(begin);
        final int m = s.getInt(begin + ((end - begin) / 2));
        final int r = s.getInt(end - 1);
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
