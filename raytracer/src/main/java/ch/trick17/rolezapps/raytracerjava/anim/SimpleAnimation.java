package ch.trick17.rolezapps.raytracerjava.anim;

import ch.trick17.rolezapps.raytracer.anim.Duration;

public abstract class SimpleAnimation extends Animation {
    
    public final Duration duration;
    public boolean begun = false;
    public boolean finished = false;
    
    public SimpleAnimation(Duration duration) {
        this.duration = duration;
    }
    
    @Override
    public boolean begun(double time) {
        if((!begun) && (time >= duration.begin)) {
            begun = true;
            onBegin();
        }
        return begun;
    }
    
    @Override
    public boolean finished(double time) {
        if((!finished) && (time >= duration.end)) {
            finished = true;
            onFinish();
        }
        return finished;
    }
    
    public void onBegin() {}
    public void onFinish() {}
}
