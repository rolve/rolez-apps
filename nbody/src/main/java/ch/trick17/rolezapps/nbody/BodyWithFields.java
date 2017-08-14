package ch.trick17.rolezapps.nbody;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import rolez.lang.Guarded;

public final class BodyWithFields extends Guarded implements BodyWithFields£position, BodyWithFields£velocity {
    
    public double x;
    
    public double y;
    
    public double z;
    
    public final double mass;
    
    public double vx;
    
    public double vy;
    
    public double vz;
    
    public BodyWithFields(final java.util.Random random, final long $task) {
        super(true);
        this.x = this.randomPos(random, $task);
        this.y = this.randomPos(random, $task);
        this.z = this.randomPos(random, $task);
        this.vx = this.randomVelocity(random, $task);
        this.vy = this.randomVelocity(random, $task);
        this.vz = this.randomVelocity(random, $task);
        this.mass = this.randomMass(random, $task);
    }
    
    public BodyWithFields(final double x, final double y, final double z, final double vx, final double vy, final double vz, final double mass, final long $task) {
        super(true);
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.mass = mass;
    }
    
    public double randomPos(final Random random, final long $task) {
        return ((2 * random.nextDouble()) - 1) * 20;
    }
    
    public double randomVelocity(final Random random, final long $task) {
        return (((2 * random.nextDouble()) - 1) * 0.01) * Constants.INSTANCE.daysPerYear;
    }
    
    public double randomMass(final Random random, final long $task) {
        return (random.nextDouble() * 0.001) * Constants.INSTANCE.solarMass;
    }
    
    public String toString(final long $task) {
        return "Body[p=(" + guardReadOnly(this, $task).x + ", " + this.y + ", " + this.z + "), v=(" + this.vx + ", " + this.vy + ", " + this.vz + "), mass=" + this.mass + "]";
    }
    
    @Override
    public String toString() {
        return this.toString(rolez.lang.Task.currentTask().idBits());
    }
    
    @Override
    public BodyWithFields $object() {
        return this;
    }
    
    private final Map<String, Guarded> $slices = new HashMap<String, Guarded>();
    
    private final BodyWithFields£position.Impl positionSlice = new BodyWithFields£position.Impl(this);
    private final BodyWithFields£velocity.Impl velocitySlice = new BodyWithFields£velocity.Impl(this);
    
    {
        $slices.put("position", positionSlice);
        $slices.put("velocity", velocitySlice);
    }
    
    @Override
    protected final java.util.Collection<rolez.lang.Guarded> views() {
        return $slices.values();
    }
    
    @Override
    protected final BodyWithFields viewLock() {
        return this;
    }
    
    public final BodyWithFields£position $positionSlice() {
        return positionSlice;
    }
    
    public final BodyWithFields£velocity $velocitySlice() {
        return velocitySlice;
    }
}
