package ch.trick17.rolezapps.raytracerjava;

public class Sphere extends Primitive {
    
    public Vector3D center;
    public double radius;
    
    public Sphere(Vector3D center, double radius, Material mat) {
        super(mat);
        this.center = center;
        this.radius = radius;
    }
    
    @Override
    public void move(Vector3D delta) {
        center = center.plus(delta);
    }
    
    @Override
    public Vector3D normal(Vector3D point) {
        return point.minus(center).normalize();
    }
    
    @Override
    public Intersection intersect(final Ray ray) {
        Vector3D diff = center.minus(ray.origin);
        double b = diff.dot(ray.direction);
        double r2 = radius * radius;
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
