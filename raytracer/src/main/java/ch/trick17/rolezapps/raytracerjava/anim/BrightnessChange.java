package ch.trick17.rolezapps.raytracerjava.anim;

import ch.trick17.rolezapps.raytracer.anim.Duration;
import ch.trick17.rolezapps.raytracerjava.Light;

public class BrightnessChange extends SimpleAnimation {
    
    public final Light light;
    public final double changePerS;
    
    public BrightnessChange(Duration duration, Light light, double changePerS) {
        super(duration);
        this.light = light;
        this.changePerS = changePerS;
    }
    
    @Override
    public void animationStep(double time, double timeStep) {
        light.brightness = Math.max(0.0, light.brightness + changePerS * timeStep);
    }
}
