package ch.trick17.rolezapps.raytracerjava.anim;

import ch.trick17.rolezapps.raytracer.Color;
import ch.trick17.rolezapps.raytracer.anim.Duration;
import ch.trick17.rolezapps.raytracerjava.Scene;

public class BackgroundChange extends SimpleAnimation {
    
    public final Scene scene;
    public final double rChangePerS;
    public final double gChangePerS;
    public final double bChangePerS;
    
    public BackgroundChange(Duration duration, Scene scene, double rChangePerS, double gChangePerS,
            double bChangePerS) {
        super(duration);
        this.scene = scene;
        this.rChangePerS = rChangePerS;
        this.gChangePerS = gChangePerS;
        this.bChangePerS = bChangePerS;
    }
    
    @Override
    public void animationStep(double time, int f) {
        final Color bg = scene.background;
        scene.background = new Color(bg.r + (rChangePerS / f), bg.g + (gChangePerS / f),
                bg.b + (bChangePerS / f));
    }
}
