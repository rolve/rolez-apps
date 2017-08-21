package ch.trick17.rolezapps.raytracer.anim;

import java.io.IOException;
import java.util.Random;

import ch.trick17.rolezapps.raytracer.util.VideoWriterJava;
import ch.trick17.rolezapps.raytracerjava.Raytracer;
import ch.trick17.rolezapps.raytracerjava.anim.AnimatedScene;
import ch.trick17.rolezapps.raytracerjava.anim.Animator;
import ch.trick17.rolezapps.raytracerjava.anim.AnimatorApp;

public class AnimatorBenchmarkSetupJava extends AnimatorBenchmarkSetup {
    
    private final Animator animator;
    
    public AnimatorBenchmarkSetupJava(int height, int numTasks, long $task) {
        super($task);
        
        AnimatedScene scene = new AnimatedScene(3.0);
        AnimatorApp.buildScene(scene, new Random(42));
        int width = (int) (height * scene.view.aspect);
        VideoWriterJava writer;
        try {
            writer = new VideoWriterJava(movieFile, width, height, 12, 12);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        
        Raytracer raytracer = new Raytracer();
        raytracer.numTasks = numTasks;
        raytracer.maxRecursions = 3;
        this.animator = new Animator(raytracer, scene, writer);
    }
    
    @Override
    public void runAnimator$Unguarded(long $task) {
        try {
            animator.render();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
