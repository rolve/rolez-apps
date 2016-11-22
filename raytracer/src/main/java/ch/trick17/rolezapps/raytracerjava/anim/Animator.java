package ch.trick17.rolezapps.raytracerjava.anim;

import java.io.IOException;

import ch.trick17.rolezapps.raytracer.util.VideoWriterJava;
import ch.trick17.rolezapps.raytracerjava.Raytracer;

public class Animator {
    
    public final Raytracer raytracer;
    public final AnimatedScene scene;
    public final VideoWriterJava writer;
    public boolean printProgress = false;
    public final int frames;
    public int frame;
    
    public Animator(Raytracer raytracer, AnimatedScene scene, VideoWriterJava writer) {
        this.raytracer = raytracer;
        this.scene = scene;
        this.writer = writer;
        this.frames = (int) Math.ceil(scene.length * writer.framerate);
    }
    
    public double time() {
        return ((double) frame) / writer.framerate;
    }
    
    public void render() throws IOException {
        frame = 0;
        raytracer.scene = scene;
        int[][] image = new int[writer.height][writer.width];
        while(frame < frames) {
            raytracer.render(image);
            writer.writeFrame(image);
            frame += 1;
            scene.animate(time(), writer.framerate);
            if(printProgress)
                System.out.println((("Frame " + frame) + "/") + frames);
        }
        writer.close();
    }
}
