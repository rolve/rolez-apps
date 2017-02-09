package ch.trick17.rolezapps.raytracerintersectopt;

import ch.trick17.rolezapps.raytracer.Intersection;
import ch.trick17.rolezapps.raytracer.Material;
import ch.trick17.rolezapps.raytracer.Ray;
import ch.trick17.rolezapps.raytracer.Vector3D;
import rolez.lang.Task;

public class Sphere extends Primitive {
    
    public ch.trick17.rolezapps.raytracer.Vector3D center;
    public double radius;
    
    public Sphere(Vector3D center, double radius, Material mat) {
        super(mat);
        this.center = center;
        this.radius = radius;
    }
    
    @Override
    public void move(Vector3D delta) {
        guardReadWrite(this).center = this.center.plus(delta);
    }
    
    @Override
    public Vector3D normal(Vector3D point) {
        return point.minus(guardReadOnly(this).center).normalize();
    }
    
    @Override
    public Intersection intersect(Ray ray, Task<?> currentTask) {
        Vector3D diff = guardReadOnly(this, currentTask).center.minus(ray.origin);
        double b = diff.dot(ray.direction);
        double r2 = this.radius * this.radius;
        double discr2 = ((b * b) - diff.dot(diff)) + r2;
        if(discr2 < 0)
            return null;
        
        double discr = Math.sqrt(discr2);
        double t;
        if((b - discr) < 0.000001)
            t = b + discr;
        else
            t = b - discr;
        
        if(t < 0.000001)
            return null;
        else
            return new Intersection(t, diff.dot(diff) > (r2 + 0.000001), this);
    }
}
