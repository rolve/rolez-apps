package ch.trick17.rolezapps.histogram;

import static rolez.lang.Guarded.*;

import rolez.lang.GuardedArray;

public class HistogramRolezOpt extends HistogramRolez {
    
    public HistogramRolezOpt(final GuardedArray<GuardedArray<int[]>[]> image, final long $task) {
        super(image, $task);
    }
    
    @Override
    public void compute$Unguarded(final int numTasks, final long $task) {
        final rolez.lang.SliceRange[] ranges = rolez.lang.ContiguousPartitioner.INSTANCE.partition$Unguarded(this.image.range, numTasks, $task);
        final rolez.lang.GuardedArray<ch.trick17.rolezapps.histogram.HistPart[]> parts = new rolez.lang.GuardedArray<ch.trick17.rolezapps.histogram.HistPart[]>(new ch.trick17.rolezapps.histogram.HistPart[numTasks]);
        { /* parfor */
            final java.util.List<java.lang.Object[]> $argsList = new java.util.ArrayList<>();
            for(int i = 0; i < numTasks; i++)
                $argsList.add(new java.lang.Object[] {this, ranges[i], parts.slice(i, i + 1), i});
            
            final java.lang.Object[][] $passed = new java.lang.Object[$argsList.size()][];
            final java.lang.Object[][] $shared = new java.lang.Object[$argsList.size()][];
            for(int $i = 0; $i < $argsList.size(); $i++) {
                final java.lang.Object[] $args = $argsList.get($i);
                $passed[$i] = new java.lang.Object[] {$args[2]};
                $shared[$i] = new java.lang.Object[] {$args[0]};
            }
            
            final rolez.lang.Task<?>[] $tasks = new rolez.lang.Task<?>[$argsList.size()];
            long $tasksBits = 0;
            for(int $i = 0; $i < $tasks.length; $i++) {
                final java.lang.Object[] $args = $argsList.get($i);
                $tasks[$i] = new rolez.lang.Task<java.lang.Void>($passed[$i], $shared[$i], $tasksBits, true) {
                    @java.lang.Override
                    protected java.lang.Void runRolez() {
                        ((ch.trick17.rolezapps.histogram.HistogramRolez) $args[0]).computePart$Unguarded((rolez.lang.SliceRange) $args[1], (rolez.lang.GuardedSlice<ch.trick17.rolezapps.histogram.HistPart[]>) $args[2], (int) $args[3], idBits());
                        return null;
                    }
                };
                $tasksBits |= $tasks[$i].idBits();
            }
            
            try {
                for(int $i = 0; $i < $tasks.length-1; $i++)
                    rolez.lang.TaskSystem.getDefault().start($tasks[$i]);
                rolez.lang.TaskSystem.getDefault().run($tasks[$tasks.length - 1]);
            } finally {
                for(rolez.lang.Task<?> $t : $tasks)
                    $t.get();
            }
        }
        
        this.rHist = parts.data[0].r;
        this.gHist = parts.data[0].g;
        this.bHist = parts.data[0].b;
        for(int i = 1; i < numTasks; i++)
            this.merge$Unguarded(parts.data[i], $task);
    }
}
