package ch.trick17.rolezapps.raytracerjava.anim;

import java.util.ArrayList;

import ch.trick17.rolezapps.raytracerjava.Scene;

public class AnimatedScene extends Scene {
    
    public final double length;
    public final ArrayList<Animation> animations = new ArrayList<Animation>();
    
    public AnimatedScene(double length) {
        this.length = length;
    }
    
    public void animate(double time, int framerate) {
        for(int i = 0; i < animations.size(); i += 1) {
            Animation animation = animations.get(i);
            if(animation.begun(time) && (!animation.finished(time)))
                animation.animationStep(time, framerate);
        }
    }
}
