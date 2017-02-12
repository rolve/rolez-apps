package ch.trick17.rolezapps.raytracer;

import ch.trick17.rolezapps.raytracer.anim.AnimatorApp;
import ch.trick17.rolezapps.raytracerintersectopt.anim.AnimatedScene;
import rolez.lang.Task;
import rolez.util.Random;

public class RaytracerBenchmarkSetupIntersectOpt extends RaytracerBenchmarkSetupRolez {
    
    public RaytracerBenchmarkSetupIntersectOpt(int height, int numTasks, Random random,
            Task<?> $task) {
        super(height, numTasks, random, $task);
    }
    
    @Override
    public Scene createBenchmarkScene(Random random, Task<?> $task) {
        AnimatedScene scene = new AnimatedScene(30, $task);
        AnimatorApp.INSTANCE.buildScene(scene, random, $task);
        for(int t = 1; t <= 8; t += 1)
            scene.animationStep(1.0, $task);
        return scene;
    }
}
