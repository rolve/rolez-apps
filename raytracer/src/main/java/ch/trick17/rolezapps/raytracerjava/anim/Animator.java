package ch.trick17.rolezapps.raytracerjava.anim;

import java.io.IOException;

import ch.trick17.rolezapps.raytracer.util.VideoWriterJava;
import ch.trick17.rolezapps.raytracerjava.Raytracer;

public class Animator {
    
    public final Raytracer raytracer;
    public final AnimatedScene scene;
    public final VideoWriterJava writer;
    
    public boolean printProgress = false;
    
    public Animator(Raytracer raytracer, AnimatedScene scene, VideoWriterJava writer) {
        this.raytracer = raytracer;
        this.scene = scene;
        this.writer = writer;
    }
    
    public void render() throws IOException {
        raytracer.scene = scene;
        
        int[][] image = new int[writer.height][writer.width];
        
        while(scene.time < scene.length) {
            raytracer.render(image);
            writer.writeFrame(image);
            
            scene.animationStep(1.0 / writer.framerate);
            if(printProgress)
                System.out.println(scene.time + "/" + scene.length);
        }
        writer.close();
    }
}
