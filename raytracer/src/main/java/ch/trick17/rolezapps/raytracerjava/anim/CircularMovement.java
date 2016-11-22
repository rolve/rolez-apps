package ch.trick17.rolezapps.raytracerjava.anim;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import ch.trick17.rolezapps.raytracer.Vector3D;
import ch.trick17.rolezapps.raytracer.anim.Duration;

public class CircularMovement extends SimpleAnimation {
    
    public final Moveable obj;
    public final Vector3D u1;
    public final Vector3D u2;
    
    public final double thetaPerS;
    
    public CircularMovement(Duration duration, Moveable obj, Vector3D toCenter, Vector3D up,
            final double thetaPerS) {
        super(duration);
        this.obj = obj;
        this.u1 = toCenter.negate();
        this.u2 = up.cross(u1).normalize().scale(u1.length());
        this.thetaPerS = thetaPerS;
    }
    
    @Override
    public void animationStep(double time, double timeStep) {
        double oldTheta = (time - duration.begin - timeStep) * thetaPerS;
        double newTheta = (time - duration.begin) * thetaPerS;
        Vector3D delta = u1.scale(cos(newTheta) - cos(oldTheta))
                .plus(u2.scale(sin(newTheta) - sin(oldTheta)));
        obj.move(delta);
    }
}
