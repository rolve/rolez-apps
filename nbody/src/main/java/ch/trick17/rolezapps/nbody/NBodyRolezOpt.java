package ch.trick17.rolezapps.nbody;

public class NBodyRolezOpt extends NBodyRolez {
    
    public NBodyRolezOpt(final long $task) {
        super(10000, 10, 2, $task);
    }
    
    public NBodyRolezOpt(final int bodies, final int iterations, final int tasks, final long $task) {
        super(bodies, iterations, tasks, $task);
    }
    
    @Override
    public void simulationStep$Unguarded(final long $task) {
        final rolez.lang.GuardedArray<ch.trick17.rolezapps.nbody.Body£position[]> positions = new rolez.lang.GuardedArray<ch.trick17.rolezapps.nbody.Body£position[]>(new ch.trick17.rolezapps.nbody.Body£position[this.bodies]);
        final rolez.lang.GuardedArray<ch.trick17.rolezapps.nbody.Body£velocity[]> velocities = new rolez.lang.GuardedArray<ch.trick17.rolezapps.nbody.Body£velocity[]>(new ch.trick17.rolezapps.nbody.Body£velocity[this.bodies]);
        for(int i = 0; i < this.bodies; i++) {
            positions.data[i] = this.system.data[i].$positionSlice();
            velocities.data[i] = this.system.data[i].$velocitySlice();
        }
        final rolez.lang.GuardedArray<rolez.lang.GuardedSlice<ch.trick17.rolezapps.nbody.Body£velocity[]>[]> vSlices = velocities.partition(rolez.lang.ContiguousPartitioner.INSTANCE, this.tasks);
        { /* parfor */
            final java.util.List<java.lang.Object[]> $argsList = new java.util.ArrayList<>();
            for(int t = 0; t < this.tasks; t++)
                $argsList.add(new java.lang.Object[] {this, vSlices.data[t], positions});
            
            final java.lang.Object[][] $passed = new java.lang.Object[$argsList.size()][];
            final java.lang.Object[][] $shared = new java.lang.Object[$argsList.size()][];
            for(int $i = 0; $i < $argsList.size(); $i++) {
                final java.lang.Object[] $args = $argsList.get($i);
                $passed[$i] = new java.lang.Object[] {$args[1]};
                $shared[$i] = new java.lang.Object[] {$args[2]};
            }
            
            final rolez.lang.Task<?>[] $tasks = new rolez.lang.Task<?>[$argsList.size()];
            long $tasksBits = 0;
            for(int $i = 0; $i < $tasks.length; $i++) {
                final java.lang.Object[] $args = $argsList.get($i);
                $tasks[$i] = new rolez.lang.Task<java.lang.Void>($passed[$i], $shared[$i], $tasksBits, true) {
                    @java.lang.Override
                    protected java.lang.Void runRolez() {
                        ((ch.trick17.rolezapps.nbody.NBodyRolez) $args[0]).updateVelocity$Unguarded((rolez.lang.GuardedSlice<ch.trick17.rolezapps.nbody.Body£velocity[]>) $args[1], (rolez.lang.GuardedArray<ch.trick17.rolezapps.nbody.Body£position[]>) $args[2], idBits());
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
        
        final rolez.lang.GuardedArray<rolez.lang.GuardedSlice<ch.trick17.rolezapps.nbody.Body[]>[]> bodiesSlices = this.system.partition(rolez.lang.ContiguousPartitioner.INSTANCE, this.tasks);
        { /* parfor */
            final java.util.List<java.lang.Object[]> $argsList = new java.util.ArrayList<>();
            for(int t = 0; t < this.tasks; t++)
                $argsList.add(new java.lang.Object[] {this, bodiesSlices.data[t]});
            
            final java.lang.Object[][] $passed = new java.lang.Object[$argsList.size()][];
            final java.lang.Object[][] $shared = new java.lang.Object[$argsList.size()][];
            for(int $i = 0; $i < $argsList.size(); $i++) {
                final java.lang.Object[] $args = $argsList.get($i);
                $passed[$i] = new java.lang.Object[] {$args[1]};
                $shared[$i] = new java.lang.Object[] {};
            }
            
            final rolez.lang.Task<?>[] $tasks = new rolez.lang.Task<?>[$argsList.size()];
            long $tasksBits = 0;
            for(int $i = 0; $i < $tasks.length; $i++) {
                final java.lang.Object[] $args = $argsList.get($i);
                $tasks[$i] = new rolez.lang.Task<java.lang.Void>($passed[$i], $shared[$i], $tasksBits, true) {
                    @java.lang.Override
                    protected java.lang.Void runRolez() {
                        ((ch.trick17.rolezapps.nbody.NBodyRolez) $args[0]).updatePosition$Unguarded((rolez.lang.GuardedSlice<ch.trick17.rolezapps.nbody.Body[]>) $args[1], idBits());
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
    }
}
