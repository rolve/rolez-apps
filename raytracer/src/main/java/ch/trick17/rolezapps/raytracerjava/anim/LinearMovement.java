package ch.trick17.rolezapps.raytracerjava.anim;

import ch.trick17.rolezapps.raytracer.Vector3D;
import ch.trick17.rolezapps.raytracer.anim.Duration;

public class LinearMovement extends SimpleAnimation {
    
    public final Moveable obj;
    public final Vector3D deltaPerS;
    
    public LinearMovement(Duration duration, Moveable obj, Vector3D deltaPerS) {
        super(duration);
        this.obj = obj;
        this.deltaPerS = deltaPerS;
    }
    
    @Override
    public void animationStep(double time, double timeStep) {
        obj.move(deltaPerS.scale(timeStep));
    }
}
