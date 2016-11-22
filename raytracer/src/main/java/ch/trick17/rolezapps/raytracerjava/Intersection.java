package ch.trick17.rolezapps.raytracerjava;

public class Intersection {
    
    public final double t;
    
    public final boolean enter;
    
    public final Primitive primitive;
    
    public Intersection(final double t, final boolean enter, final Primitive primitive) {
        super();
        this.t = t;
        this.enter = enter;
        this.primitive = primitive;
    }
}
