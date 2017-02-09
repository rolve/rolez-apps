package ch.trick17.rolezapps.raytracerintersectopt;

import ch.trick17.rolezapps.raytracer.Intersection;
import ch.trick17.rolezapps.raytracer.Material;
import ch.trick17.rolezapps.raytracer.Ray;
import rolez.lang.Task;

public abstract class Primitive extends ch.trick17.rolezapps.raytracer.Primitive {
    
    public Primitive(final Material mat) {
        super(mat);
    }
    
    @Override
    public final Intersection intersect(Ray ray) {
        throw new AssertionError();
    }
    
    public abstract Intersection intersect(final Ray ray, Task<?> currentTask);
}
