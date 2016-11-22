package ch.trick17.rolezapps.raytracerjava.anim;

import ch.trick17.rolezapps.raytracer.anim.Duration;
import ch.trick17.rolezapps.raytracerjava.Scene;

public class AmbientLightChange extends SimpleAnimation {
    
    public final Scene scene;
    public final double changePerS;
    
    public AmbientLightChange(Duration duration, Scene scene, double changePerS) {
        super(duration);
        this.scene = scene;
        this.changePerS = changePerS;
    }
    
    @Override
    public void animationStep(double time, int framerate) {
        scene.ambientLight += changePerS / framerate;
    }
}
