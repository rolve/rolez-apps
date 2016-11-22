package ch.trick17.rolezapps.raytracerjava;

import ch.trick17.rolezapps.raytracer.Ray;
import ch.trick17.rolezapps.raytracer.Vector3D;
import ch.trick17.rolezapps.raytracerjava.anim.Moveable;

public class Primitive extends Moveable {
    
    public Material mat;
    
    public Primitive(final Material mat) {
        super();
        this.mat = mat;
    }
    
    public Vector3D normal(final Vector3D point) {
        return null;
    }
    
    public Intersection intersect(final Ray ray) {
        return null;
    }
}
