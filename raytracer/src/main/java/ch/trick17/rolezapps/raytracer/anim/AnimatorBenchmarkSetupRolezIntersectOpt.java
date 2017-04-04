package ch.trick17.rolezapps.raytracer.anim;

import ch.trick17.rolezapps.raytracerintersectopt.anim.AnimatedScene;

public class AnimatorBenchmarkSetupRolezIntersectOpt extends AnimatorBenchmarkSetupRolez {
    
    public AnimatorBenchmarkSetupRolezIntersectOpt(int height, int numTasks, long $task) {
        super(height, numTasks, $task);
    }
    
    @Override
    public AnimatedScene instantiateScene(long $task) {
        return new AnimatedScene(3.0, $task);
    }
}
