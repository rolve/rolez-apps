package ch.trick17.rolezapps.raytracerjava;

import java.util.ArrayList;

import ch.trick17.rolezapps.raytracer.Color;
import rolez.lang.Task;

public class Scene extends ch.trick17.rolezapps.raytracer.Scene {
    
    public View view;
    public final ArrayList<Primitive> objects = new ArrayList<Primitive>();
    public final ArrayList<Light> lights = new ArrayList<Light>();
    public double ambientLight;
    public Color background;
    
    public Scene() {
        super(null);
    }
    
    @Override
    public ch.trick17.rolezapps.raytracer.Intersection intersect(
            ch.trick17.rolezapps.raytracer.Ray ray, Task<?> $task) {
        throw new AssertionError();
    }
    
    public Intersection intersectJava(final Ray ray) {
        Intersection closest = null;
        for(int i = 0; i < this.objects.size(); i += 1) {
            final Intersection intersection = this.objects.get(i).intersect(ray);
            if((intersection != null) && ((closest == null) || (intersection.t < closest.t)))
                closest = intersection;
        }
        return closest;
    }
}
