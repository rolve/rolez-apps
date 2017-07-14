package ch.trick17.rolezapps.nbody;

import java.util.Random;

public final class BodyJava {
    
    public double x;
    public double y;
    public double z;

    public double vx;
    public double vy;
    public double vz;
    
    public final double mass;
    
    public BodyJava( Random random) {
        this.x = randomPos(random);
        this.y = randomPos(random);
        this.z = randomPos(random);
        this.vx = randomVelocity(random);
        this.vy = randomVelocity(random);
        this.vz = randomVelocity(random);
        this.mass = randomMass(random);
    }
    
    public BodyJava(double x, double y, double z, double vx, double vy, double vz, double mass) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.mass = mass;
    }
    
    public static double randomPos(Random random) {
        return (2 * random.nextDouble() - 1) * 20;
    }
    
    public static double randomVelocity(Random random) {
        return ((2 * random.nextDouble() - 1) * 0.01) * Constants.INSTANCE.daysPerYear;
    }
        
    public static double randomMass(Random random) {
        return (random.nextDouble() * 0.001) * Constants.INSTANCE.solarMass;
    }
    
    @Override
    public String toString() {
        return "Body[p=(" + x + ", " + y + ", " + z + "), v=(" + vx + ", " + vy + ", " + vz + "), mass=" + mass + "]";
    }
}
