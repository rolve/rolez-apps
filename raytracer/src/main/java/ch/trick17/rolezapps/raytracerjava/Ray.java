package ch.trick17.rolezapps.raytracerjava;

public class Ray {
    
    public final Vector3D origin;
    public final Vector3D direction;
    
    public Ray(Vector3D origin, Vector3D direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }
    
    public Vector3D point(double t) {
        return origin.plus(direction.scale(t));
    }
    
    @Override
    public String toString() {
        return origin + " -> " + direction;
    }
}
