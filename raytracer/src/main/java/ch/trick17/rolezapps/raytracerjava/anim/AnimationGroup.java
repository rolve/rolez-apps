package ch.trick17.rolezapps.raytracerjava.anim;

public class AnimationGroup extends Animation {
    
    public final Animation[] animations;
    public boolean firstBegun = false;
    public boolean lastFinished = false;
    
    public AnimationGroup(Animation[] animations) {
        this.animations = animations;
    }
    
    @Override
    public boolean begun(double time) {
        boolean oneBegun = false;
        for(Animation element : animations) {
            if(element.begun(time))
                oneBegun = true;
        }
        if((!firstBegun) && oneBegun) {
            firstBegun = true;
            onFirstBegin();
        }
        
        return oneBegun;
    }
    
    @Override
    public boolean finished(double time) {
        boolean allFinished = true;
        for(int i = 0; i < animations.length; i += 1) {
            if(!animations[i].finished(time))
                allFinished = false;
        }
        if((!lastFinished) && allFinished) {
            lastFinished = true;
            onLastFinish();
        }
        
        return allFinished;
    }
    
    @Override
    public void animationStep(double time, int framerate) {
        for(Animation element : animations) {
            final Animation animation = element;
            if(animation.begun(time) && (!animation.finished(time)))
                animation.animationStep(time, framerate);
        }
    }
    
    public void onFirstBegin() {}
    
    public void onLastFinish() {}
}
