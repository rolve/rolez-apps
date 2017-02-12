package ch.trick17.rolezapps.raytracerjava;

public class Plane extends Primitive {
    
    public Vector3D normal;
    
    public double distance;
    
    public Plane(final Vector3D normal, final double distance, final Material mat) {
        super(mat);
        this.normal = normal;
        this.distance = distance;
    }
    
    @Override
    public void move(final Vector3D delta) {
    }
    
    @Override
    public Vector3D normal(final Vector3D point) {
        return this.normal.normalize();
    }
    
    @Override
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
