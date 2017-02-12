package ch.trick17.rolezapps.raytracerjava;

import ch.trick17.rolezapps.raytracer.Color;

public class ColorBuilder {
    
    public double r;
    
    public double g;
    
    public double b;
    
    public ColorBuilder() {
        super();
    }
    
    public Color build() {
        return new Color(this.r, this.g, this.b, 0L);
    }
    
    public void add(final Color color, final double multiplier) {
        this.r += multiplier * color.r;
        this.g += multiplier * color.g;
        this.b += multiplier * color.b;
    }
    
    public void add(final Color color) {
        this.r += color.r;
        this.g += color.g;
        this.b += color.b;
    }
    
    public void add(final double value) {
        this.r += value;
        this.g += value;
        this.b += value;
    }
    
    public void multiply(final double value) {
        this.r = this.r * value;
        this.g = this.g * value;
        this.b = this.b * value;
    }
}
