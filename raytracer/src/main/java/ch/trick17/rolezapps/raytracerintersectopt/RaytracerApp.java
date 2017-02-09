package ch.trick17.rolezapps.raytracerintersectopt;

import static rolez.lang.Guarded.guardReadOnly;
import static rolez.lang.GuardedArray.unwrap;

import java.io.IOException;
import java.util.Scanner;

import ch.trick17.rolezapps.raytracer.Raytracer;
import ch.trick17.rolezapps.raytracer.util.ImageWriterJava;
import ch.trick17.rolezapps.raytracerintersectopt.anim.AnimatedScene;
import ch.trick17.rolezapps.raytracerintersectopt.anim.AnimatorApp;
import rolez.lang.GuardedArray;
import rolez.lang.Task;
import rolez.lang.TaskSystem;
import rolez.util.Random;
import rolez.util.StopWatch;

public final class RaytracerApp {
    
    public static final RaytracerApp INSTANCE = new RaytracerApp();
    
    private RaytracerApp() {}
    
    public static Task<Void> $mainTask(final GuardedArray<String[]> args) {
        return new Task<Void>(new Object[]{}, new Object[]{args}) {
            @Override
            protected Void runRolez() {
                try {
                    AnimatedScene scene = new AnimatedScene(30.0);
                    AnimatorApp.INSTANCE.buildScene(scene, new Random(42));
                    for(int i = 0; i < 8; i += 1)
                        scene.animationStep(1.0);
                    
                    Raytracer raytracer = new Raytracer();
                    raytracer.numTasks = 8;
                    raytracer.maxRecursions = 5;
                    raytracer.oversample = 2;
                    raytracer.scene = scene;
                    
                    int height = 180;
                    int width = (int) (guardReadOnly(scene.view).aspect * height);
                    GuardedArray<GuardedArray<int[]>[]> image = new GuardedArray<GuardedArray<int[]>[]>(
                            new GuardedArray[height]);
                    for(int i = 0; i < height; i += 1)
                        image.data[i] = new GuardedArray<int[]>(new int[width]);
                    
                    System.out.println("Press Enter to start");
                    new Scanner(System.in).nextLine();
                    for(int i = 0; i < 10; i += 1) {
                        StopWatch watch = new StopWatch().go();
                        raytracer.render(image);
                        System.out.println(watch.get());
                    }
                    ImageWriterJava.write(unwrap(image, int[][].class), "png", "image.png");
                }
                catch(IOException e) {
                    throw new RuntimeException("ROLEZ EXCEPTION WRAPPER", e);
                }
                return null;
            }
        };
    }
    
    public static void main(final String[] args) {
        TaskSystem.getDefault().run($mainTask(GuardedArray.<String[]> wrap(args)));
    }
}
