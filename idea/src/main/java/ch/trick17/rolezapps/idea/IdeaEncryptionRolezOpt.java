package ch.trick17.rolezapps.idea;

public class IdeaEncryptionRolezOpt extends ch.trick17.rolezapps.idea.IdeaEncryptionRolez {
    
    public IdeaEncryptionRolezOpt(final int size, final int tasks, final long $task) {
        super(size, tasks, $task);
    }
    
    @Override
    public void run$Unguarded(final long $task) {
        final rolez.lang.BlockPartitioner partitioner = new rolez.lang.BlockPartitioner(8, $task);
        final rolez.lang.GuardedArray<rolez.lang.GuardedSlice<byte[]>[]> plainSlices = this.plain.partition(partitioner, this.tasks);
        final rolez.lang.GuardedArray<rolez.lang.GuardedSlice<byte[]>[]> encryptedSlices = this.encrypted.partition(partitioner, this.tasks);
        final rolez.lang.GuardedArray<rolez.lang.GuardedSlice<byte[]>[]> decryptedSlices = this.decrypted.partition(partitioner, this.tasks);
        { /* parfor */
            final java.util.List<java.lang.Object[]> $argsList = new java.util.ArrayList<>();
            for(int i = 0; i < this.tasks; i++)
                $argsList.add(new java.lang.Object[] {this, plainSlices.data[i], encryptedSlices.data[i], this.encryptKey});
            
            final java.lang.Object[][] $passed = new java.lang.Object[$argsList.size()][];
            final java.lang.Object[][] $shared = new java.lang.Object[$argsList.size()][];
            for(int $i = 0; $i < $argsList.size(); $i++) {
                final java.lang.Object[] $args = $argsList.get($i);
                $passed[$i] = new java.lang.Object[] {$args[2]};
                $shared[$i] = new java.lang.Object[] {$args[1]};
            }
            
            final rolez.lang.Task<?>[] $tasks = new rolez.lang.Task<?>[$argsList.size()];
            long $tasksBits = 0;
            for(int $i = 0; $i < $tasks.length; $i++) {
                final java.lang.Object[] $args = $argsList.get($i);
                $tasks[$i] = new rolez.lang.Task<java.lang.Void>($passed[$i], $shared[$i], $tasksBits, true) {
                    @java.lang.Override
                    protected java.lang.Void runRolez() {
                        ((ch.trick17.rolezapps.idea.IdeaEncryptionRolezOpt) $args[0]).encryptDecrypt$Unguarded((rolez.lang.GuardedSlice<byte[]>) $args[1], (rolez.lang.GuardedSlice<byte[]>) $args[2], (int[]) $args[3], idBits());
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
        
        { /* parfor */
            final java.util.List<java.lang.Object[]> $argsList = new java.util.ArrayList<>();
            for(int i = 0; i < this.tasks; i++)
                $argsList.add(new java.lang.Object[] {this, encryptedSlices.data[i], decryptedSlices.data[i], this.decryptKey});
            
            final java.lang.Object[][] $passed = new java.lang.Object[$argsList.size()][];
            final java.lang.Object[][] $shared = new java.lang.Object[$argsList.size()][];
            for(int $i = 0; $i < $argsList.size(); $i++) {
                final java.lang.Object[] $args = $argsList.get($i);
                $passed[$i] = new java.lang.Object[] {$args[2]};
                $shared[$i] = new java.lang.Object[] {$args[1]};
            }
            
            final rolez.lang.Task<?>[] $tasks = new rolez.lang.Task<?>[$argsList.size()];
            long $tasksBits = 0;
            for(int $i = 0; $i < $tasks.length; $i++) {
                final java.lang.Object[] $args = $argsList.get($i);
                $tasks[$i] = new rolez.lang.Task<java.lang.Void>($passed[$i], $shared[$i], $tasksBits, true) {
                    @java.lang.Override
                    protected java.lang.Void runRolez() {
                        ((ch.trick17.rolezapps.idea.IdeaEncryptionRolezOpt) $args[0]).encryptDecrypt$Unguarded((rolez.lang.GuardedSlice<byte[]>) $args[1], (rolez.lang.GuardedSlice<byte[]>) $args[2], (int[]) $args[3], idBits());
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
