package ch.trick17.rolezapps.raytracerjava.anim;

import ch.trick17.rolezapps.raytracer.Vector3D;
import ch.trick17.rolezapps.raytracer.anim.Duration;
import ch.trick17.rolezapps.raytracerjava.Sphere;

public class BallAnimation extends LinearMovement {
    
    public final AnimatedScene scene;
    
    public BallAnimation(Duration duration, Sphere sphere, double speed, AnimatedScene scene) {
        super(duration, sphere, new Vector3D(0.0, 0.0, 1.0).scale(speed));
        this.scene = scene;
    }
    
    @Override
    public void onBegin() {
        scene.objects.add((Sphere) obj);
    }
    
    @Override
    public void onFinish() {
        scene.objects.remove(obj);
    }
}
