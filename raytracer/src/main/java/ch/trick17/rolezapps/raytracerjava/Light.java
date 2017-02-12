package ch.trick17.rolezapps.raytracerjava;

import ch.trick17.rolezapps.raytracerjava.anim.Moveable;

public class Light extends Moveable {
    
    public Vector3D position;
    
    public double brightness;
    
    public Light(final Vector3D position, final double brightness) {
        super();
        this.position = position;
        this.brightness = brightness;
    }
    
    @java.lang.Override
    public void move(final Vector3D delta) {
        this.position = this.position.plus(delta);
    }
}
