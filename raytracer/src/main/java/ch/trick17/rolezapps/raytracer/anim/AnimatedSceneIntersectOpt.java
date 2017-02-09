package ch.trick17.rolezapps.raytracer.anim;

import ch.trick17.rolezapps.raytracer.Intersection;
import ch.trick17.rolezapps.raytracer.Ray;

public class AnimatedSceneIntersectOpt extends AnimatedScene {
    
    public AnimatedSceneIntersectOpt(final double length) {
        super(length);
    }
    
    @Override
    public Intersection intersect(final Ray ray) {
        Intersection closest = null;
        guardReadOnly(this.objects);
        for(int i = 0; i < this.objects.size(); i += 1) {
            final Intersection intersection = this.objects.get(i).intersect(ray);
            if((intersection != null) && ((closest == null) || (intersection.t < closest.t)))
                closest = intersection;
        }
        return closest;
    }
}
