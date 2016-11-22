package ch.trick17.rolezapps.raytracerjava;

import ch.trick17.rolezapps.raytracer.Ray;
import ch.trick17.rolezapps.raytracer.Vector3D;

public class Plane extends Primitive {
    
    public Vector3D normal;
    
    public double distance;
    
    public Plane(final Vector3D normal, final double distance, final Material mat) {
        super(mat);
        this.normal = normal;
        this.distance = distance;
    }
    
    @java.lang.Override
    public void move(final Vector3D delta) {
    }
    
    @java.lang.Override
    public Vector3D normal(final Vector3D point) {
        return this.normal.normalize();
    }
    
    @java.lang.Override
    public Intersection intersect(final Ray ray) {
        final Vector3D n = this.normal.normalize();
        final double divisor = ray.direction.dot(n);
        if((divisor < 0.000001) && (divisor > (-0.000001)))
            return null;
        
        final double t = ((-this.distance) - ray.origin.dot(n)) / divisor;
        if(t < 0.000001)
            return null;
        else
            return new Intersection(t, divisor < 0, this);
    }
}
