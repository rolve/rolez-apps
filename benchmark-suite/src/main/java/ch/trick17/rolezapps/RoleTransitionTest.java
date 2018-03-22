package ch.trick17.rolezapps;

import rolez.lang.Guarded;
import rolez.lang.GuardedArray;
import rolez.lang.Task;
import rolez.lang.TaskSystem;

public class RoleTransitionTest {
    
    private static final int N = 10000;

    public static void main(String[] args) {
        System.out.println("Warming up...");
        int results = 0;
        for(int i = 0; i < 20000; i++) {
            results += test();
        }
        
        System.out.println("Alright, measure away!");
        
        while(Math.random() >= 0) {
            results += test();
        }
        
        System.out.println(results);
    }
    
    private static int test() {
        Task.registerNewRootTask();
        
        GuardedArray<Guarded[]> objects = new GuardedArray<Guarded[]>(new Guarded[N]);
        for(int i = 0; i < objects.data.length; i++) {
            objects.data[i] = new Guarded() {};
        }

        Task<Integer> task = new Task<Integer>(new Object[] {objects}, new Object[] {}) {
            protected Integer runRolez() {
                return 0;
            }
        };
        int result = TaskSystem.getDefault().run(task);
        Task.unregisterRootTask();
        return result;
    }
}
