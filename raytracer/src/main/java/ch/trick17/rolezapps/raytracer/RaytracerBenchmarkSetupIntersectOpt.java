package ch.trick17.rolezapps.raytracer;

import ch.trick17.rolezapps.raytracer.anim.AnimatedScene;
import ch.trick17.rolezapps.raytracer.anim.AnimatedSceneIntersectOpt;
import ch.trick17.rolezapps.raytracer.anim.AnimatorApp;
import rolez.util.Random;

public class RaytracerBenchmarkSetupIntersectOpt extends RaytracerBenchmarkSetupRolez {
    
    public RaytracerBenchmarkSetupIntersectOpt(int height, int numTasks, Random random) {
        super(height, numTasks, random);
    }
    
    @Override
    public Scene createBenchmarkScene(Random random) {
        AnimatedScene scene = new AnimatedSceneIntersectOpt(30);
        AnimatorApp.INSTANCE.buildScene(scene, random);
        for(int t = 1; t <= 8; t += 1)
            scene.animationStep(1.0);
        return scene;
    }
}
