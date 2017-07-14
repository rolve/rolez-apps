package ch.trick17.rolezapps.nbody;

import java.util.Random;

import rolez.lang.ContiguousPartitioner;
import rolez.lang.SliceRange;

public class NBodyJava extends NBody {
    
    public BodyJava[] system;
    
    public NBodyJava(int bodies, int iterations, int numTasks, long $task) {
        super(bodies, iterations, numTasks, $task);
    }
    
    @Override
    public void createSystem(Random random, long $task) {
        system = new BodyJava[bodies];
        double px = 0.0;
        double py = 0.0;
        double pz = 0.0;
        for (int i = 1; i < system.length; i++) {
            BodyJava body = new BodyJava(random);
            system[i] = body;
            px += body.vx * body.mass;
            py += body.vy * body.mass;
            pz += body.vz * body.mass;
        }
        double s = Constants.INSTANCE.solarMass;
        system[0] = new BodyJava(0.0, 0.0, 0.0, (-px) / s, (-py) / s, (-pz) / s, s);
    }
    
    @Override
    public void simulate(long $task) {
        for(int i = 0; i < iterations; i++)
            simulationStep();
    }
    
    public void simulationStep() {
        Thread[] threads = new Thread[tasks];
        SliceRange[] ranges = ContiguousPartitioner.INSTANCE.partition(new SliceRange(0, bodies, 1), tasks);
        for (int t = 0; t < tasks; t++) {
            Thread thread = new Thread(this.$updateVelocityTask(ranges[t]));
            threads[t] = thread;
            thread.start();
        }
        for (int t = 0; t < tasks; t++) {
            Thread thread = new Thread(this.$updatePositionTask(ranges[t]));
            threads[t] = thread;
            thread.start();        }
    }
    
    public Runnable $updateVelocityTask(final SliceRange range) {
        return new Runnable() {
            public void run() {
                for (int i = range.begin; i < range.end; i += range.step) {
                    BodyJava body1 = system[i];
                    for (int j = 0; j < system.length; j++) {
                        if (i != j) {
                            BodyJava body2 = system[j];
                            double dx = body1.x - body2.x;
                            double dy = body1.y - body2.y;
                            double dz = body1.z - body2.z;
                            double d2 = ((dx * dx) + (dy * dy)) + (dz * dz);
                            double mag = Constants.INSTANCE.dt / d2;
                            body1.vx -= (dx * body2.mass) * mag;
                            body1.vy -= (dy * body2.mass) * mag;
                            body1.vz -= (dz * body2.mass) * mag;
                        }
                    }
                }
            }
        };
    }
    
    public Runnable $updatePositionTask(final SliceRange range) {
        return new Runnable() {
            public void run() {
                for (int i = range.begin; i < range.end; i += range.step) {
                    final BodyJava body = system[i];
                    body.x += Constants.INSTANCE.dt * body.vx;
                    body.y += Constants.INSTANCE.dt * body.vy;
                    body.z += Constants.INSTANCE.dt * body.vz;
                }
            }
        };
    }
}
