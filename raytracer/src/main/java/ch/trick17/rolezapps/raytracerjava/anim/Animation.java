package ch.trick17.rolezapps.raytracerjava.anim;

public abstract class Animation {
    
    public abstract boolean begun(double time);
    
    public abstract boolean finished(double time);
    
    public abstract void animationStep(double time, double timeStep);
}
