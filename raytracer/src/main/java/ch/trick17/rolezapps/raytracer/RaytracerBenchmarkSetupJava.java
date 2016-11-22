package ch.trick17.rolezapps.raytracer;

import java.util.Random;

import ch.trick17.rolezapps.raytracerjava.Raytracer;
import ch.trick17.rolezapps.raytracerjava.Scene;
import ch.trick17.rolezapps.raytracerjava.anim.AnimatedScene;
import ch.trick17.rolezapps.raytracerjava.anim.AnimatorApp;

public class RaytracerBenchmarkSetupJava extends RaytracerBenchmarkSetup {
    
    public final Raytracer raytracer = new Raytracer();
    
    public final int[][] image;
    
    public RaytracerBenchmarkSetupJava(int height, int numTasks, Random random) {
        Scene scene = createBenchmarkScene(random);
        int width = (int) (height * scene.view.aspect);
        image = new int[height][width];
        
        raytracer.numTasks = numTasks;
        raytracer.maxRecursions = 5;
        raytracer.scene = scene;
    }
    
    public Scene createBenchmarkScene(final Random random) {
        AnimatedScene scene = AnimatorApp.createScene(random, 30.0);
        for(int t = 1; t <= 8; t += 1)
            scene.animationStep(1.0);
        return scene;
    }
    
    @Override
    public int runRaytracer() {
        raytracer.render(image);
        return image[0][0];
    }
}
