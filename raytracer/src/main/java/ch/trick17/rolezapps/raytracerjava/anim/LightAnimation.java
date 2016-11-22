package ch.trick17.rolezapps.raytracerjava.anim;

import ch.trick17.rolezapps.raytracer.Vector3D;
import ch.trick17.rolezapps.raytracer.anim.Duration;
import ch.trick17.rolezapps.raytracerjava.Light;

public class LightAnimation extends AnimationGroup {
    
    public final AnimatedScene scene;
    public final Light light;
    
    public LightAnimation(Duration duration, Light light, Vector3D center, double riseSpeed,
            double circularSpeed, AnimatedScene scene) {
        super(lightAnimations(duration, light, center, riseSpeed, circularSpeed));
        this.scene = scene;
        this.light = light;
    }
    
    private static Animation[] lightAnimations(Duration duration, Light light, Vector3D center,
            double riseSpeed, double circularSpeed) {
        Vector3D up = new Vector3D(0.0, 0.0, 1.0);
        LinearMovement linear = new LinearMovement(duration, light, up.scale(riseSpeed));
        CircularMovement circular = new CircularMovement(duration, light, center.minus(
                light.position), up, circularSpeed);
        Animation[] result = new Animation[2];
        result[0] = linear;
        result[1] = circular;
        return result;
    }
    
    @Override
    public void onFirstBegin() {
        scene.lights.add(light);
    }
    
    @Override
    public void onLastFinish() {
        scene.lights.remove(light);
    }
}
