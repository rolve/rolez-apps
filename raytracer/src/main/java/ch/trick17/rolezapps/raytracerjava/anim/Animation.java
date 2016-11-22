package ch.trick17.rolezapps.raytracerjava.anim;

public class Animation {
    
    public Animation() {
        super();
    }
    
    public boolean begun(double time) {
        return false;
    }
    
    public boolean finished(double time) {
        return false;
    }
    
    public void animationStep(double time, double timeStep) {}
}
