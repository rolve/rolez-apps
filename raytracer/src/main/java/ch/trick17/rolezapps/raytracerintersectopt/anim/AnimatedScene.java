package ch.trick17.rolezapps.raytracerintersectopt.anim;

import ch.trick17.rolezapps.raytracer.Intersection;
import ch.trick17.rolezapps.raytracer.Ray;
import rolez.lang.Task;

public class AnimatedScene extends ch.trick17.rolezapps.raytracer.anim.AnimatedScene {
    
    public AnimatedScene(final double length, Task<?> $task) {
        super(length, $task);
    }
    
    @Override
    public Intersection intersect(Ray ray, Task<?> $task) {
        Intersection closest = null;
        guardReadOnly(this.objects, $task);
        for(int i = 0; i < this.objects.size(); i += 1) {
            Intersection intersection = this.objects.get(i).intersect(ray, $task);
            if((intersection != null) && ((closest == null) || (intersection.t < closest.t)))
                closest = intersection;
        }
        return closest;
    }
}
