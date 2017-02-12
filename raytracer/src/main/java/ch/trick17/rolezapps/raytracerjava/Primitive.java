package ch.trick17.rolezapps.raytracerjava;

import ch.trick17.rolezapps.raytracerjava.anim.Moveable;

public abstract class Primitive extends Moveable {
    
    public Material mat;
    
    public Primitive(final Material mat) {
        super();
        this.mat = mat;
    }
    
    public abstract Vector3D normal(Vector3D point);
    
    public abstract Intersection intersect(Ray ray);
}
