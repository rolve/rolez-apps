package ch.trick17.rolezapps.raytracerjava;

import ch.trick17.rolezapps.raytracerjava.anim.Moveable;

public class View extends Moveable {
    
    public Vector3D from;
    public Vector3D at;
    public Vector3D up;
    public double distance;
    public double angle;
    public double aspect;
    
    @Override
    public void move(Vector3D delta) {
        from = from.plus(delta);
    }
}
