package ch.trick17.rolezapps.raytracerjava;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import ch.trick17.rolezapps.raytracer.util.ImageWriterJava;
import ch.trick17.rolezapps.raytracerjava.anim.AnimatedScene;
import ch.trick17.rolezapps.raytracerjava.anim.AnimatorApp;
import rolez.util.StopWatch;

public final class RaytracerApp {
    
    public static void main(final String[] args) throws IOException {
        AnimatedScene scene = new AnimatedScene(30.0);
        AnimatorApp.buildScene(scene, new Random(42));
        for(int i = 0; i < 8; i += 1)
            scene.animationStep(1.0);
        
        Raytracer raytracer = new Raytracer();
        raytracer.numTasks = 8;
        raytracer.maxRecursions = 5;
        raytracer.scene = scene;
        
        int height = 180;
        int width = (int) (scene.view.aspect * height);
        int[][] image = new int[height][width];
        
        System.out.println("Press Enter to start");
        new Scanner(System.in).nextLine();
        for(int i = 0; i < 10; i += 1) {
            StopWatch watch = new StopWatch().go();
            raytracer.render(image);
            System.out.println(watch.get());
        }
        ImageWriterJava.write(image, "png", "image.png");
    }
}
