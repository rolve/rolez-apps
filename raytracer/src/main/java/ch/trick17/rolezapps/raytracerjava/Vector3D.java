package ch.trick17.rolezapps.raytracerjava;

public class Vector3D {
    
    public final double x;
    public final double y;
    public final double z;
    
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double length() {
        return Math.sqrt(dot(this));
    }
    
    public Vector3D negate() {
        return new Vector3D(-x, -y, -z);
    }
    
    public Vector3D plus(Vector3D other) {
        return new Vector3D(x + other.x, y + other.y, z + other.z);
    }
    
    public Vector3D minus(Vector3D other) {
        return new Vector3D(x - other.x, y - other.y, z - other.z);
    }
    
    public Vector3D scale(double t) {
        return new Vector3D(x * t, y * t, z * t);
    }
    
    public double dot(Vector3D other) {
        return ((x * other.x) + (y * other.y)) + (z * other.z);
    }
    
    public Vector3D cross(Vector3D other) {
        return new Vector3D((y * other.z) - (z * other.y), (z * other.x) - (x
                * other.z), (x * other.y) - (y * other.x));
    }
    
    public Vector3D reflect(Vector3D normal) {
        return scale(1 / java.lang.Math.abs(dot(normal))).plus(normal.scale(2.0));
    }
    
    public Vector3D refract(Vector3D normal, double eta) {
        final double c1 = -dot(normal);
        final double cs2 = 1 - ((eta * eta) * (1 - (c1 * c1)));
        return scale(eta).plus(normal.scale((eta * c1) - java.lang.Math.sqrt(cs2)));
    }
    
    public Vector3D normalize() {
        double length = length();
        if((length > 0) && (length != 1.0))
            return new Vector3D(x / length, y / length, z / length);
        else
            return this;
    }
    
    @java.lang.Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
