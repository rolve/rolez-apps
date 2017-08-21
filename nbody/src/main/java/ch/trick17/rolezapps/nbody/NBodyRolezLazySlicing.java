package ch.trick17.rolezapps.nbody;

import static java.util.Arrays.asList;

import java.io.PrintStream;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

import rolez.internal.Tasks;
import rolez.lang.ContiguousPartitioner;
import rolez.lang.Guarded;
import rolez.lang.GuardedArray;
import rolez.lang.GuardedSlice;
import rolez.lang.SliceRange;
import rolez.lang.Task;
import rolez.lang.TaskSystem;

public class NBodyRolezLazySlicing extends ch.trick17.rolezapps.nbody.NBody {
    
    public GuardedArray<BodyWithFields[]> system;
    public PrintStream out;
    
    public NBodyRolezLazySlicing(final long $task) {
        super(10000, 10, 2, $task);
    }
    
    public NBodyRolezLazySlicing(final int bodies, final int iterations, final int tasks, final long $task) {
        super(bodies, iterations, tasks, $task);
    }
    
    @Override
    public void createSystem$Unguarded(final Random random, final long $task) {
        final GuardedArray<BodyWithFields[]> system = new GuardedArray<BodyWithFields[]>(new BodyWithFields[this.bodies]);
        double px = 0.0;
        double py = 0.0;
        double pz = 0.0;
        for(int i = 1; i < system.data.length; i++) {
            final BodyWithFields body = new BodyWithFields(random, $task);
            system.data[i] = body;
            px += body.vx * body.mass;
            py += body.vy * body.mass;
            pz += body.vz * body.mass;
        }
        final double s = Constants.INSTANCE.solarMass;
        system.data[0] = new BodyWithFields(0.0, 0.0, 0.0, (-px) / s, (-py) / s, (-pz) / s, s, $task);
        this.system = system;
    }
    
    @Override
    public void simulate$Unguarded(final long $task) {
        for(int i = 0; i < this.iterations; i++)
            this.simulationStep($task);
    }
    
    private void simulationStep(final long $task) {
        final Tasks $tasks = new Tasks();
        try {
            GuardedArray<GuardedSlice<BodyWithFields[]>[]> veloSlices = this.system.partition(ContiguousPartitioner.INSTANCE, this.tasks);
            for(int t = 0; t < this.tasks; t++)
                $tasks.addInline(TaskSystem.getDefault().start(this.updateVelocity$Task(velocityView(guardReadOnly(veloSlices, $task).data[t]), positionsView(system))));
            
            GuardedArray<GuardedSlice<BodyWithFields[]>[]> bodiesSlices = guardReadOnly(this, $task).system.partition(ContiguousPartitioner.INSTANCE, this.tasks);
            for(int t = 0; t < this.tasks; t++)
                $tasks.addInline(TaskSystem.getDefault().start(this.updatePosition$Task(guardReadOnly(bodiesSlices, $task).data[t])));
        }
        finally {
            $tasks.joinAll();
        }
    }
    
    private GuardedSliceViewWithMap<BodyWithFields, BodyWithFields£position> positionsView(GuardedSlice<BodyWithFields[]> system) {
        return new GuardedSliceViewWithMap<>(system, new Function<BodyWithFields, BodyWithFields£position>() {
            public BodyWithFields£position apply(BodyWithFields arg) {
                return arg.$positionSlice();
            }
        });
    }
    
    private GuardedSliceViewWithMap<BodyWithFields, BodyWithFields£velocity> velocityView(GuardedSlice<BodyWithFields[]> system) {
        return new GuardedSliceViewWithMap<>(system, new Function<BodyWithFields, BodyWithFields£velocity>() {
            public BodyWithFields£velocity apply(BodyWithFields arg) {
                return arg.$velocitySlice();
            }
        });
    }

    private static final class GuardedSliceViewWithMap<E, M> extends Guarded implements RandomAccess {
        final E[] data;
        final SliceRange range;
        final Function<E, M> mapper;

        GuardedSliceViewWithMap(GuardedSlice<E[]> slice, Function<E, M> mapper) {
            this.data = slice.data;
            this.range = slice.range;
            this.mapper = mapper;
        }
        
        M get(int index) {
            return mapper.apply(data[index]);
        }
    }
    
    private interface Function<A, R> {
        R apply(A arg);
    }
    
    private Task<Void> updateVelocity$Task(final GuardedSliceViewWithMap<?, BodyWithFields£velocity> velocities, final GuardedSliceViewWithMap<?, BodyWithFields£position> positions) {
        return new Task<Void>(new Object[]{velocities}, new Object[]{positions}) {
            @Override
            protected Void runRolez() {
                for(int i = velocities.range.begin; i < velocities.range.end; i += velocities.range.step) {
                    final BodyWithFields£velocity v1 = velocities.get(i);
                    final BodyWithFields£position p1 = positions.get(i);
                    for(int j = 0; j < positions.data.length; j++) {
                        if(i != j) {
                            final BodyWithFields£position p2 = positions.get(j);
                            final double dx = p1.$object().x - p2.$object().x;
                            final double dy = p1.$object().y - p2.$object().y;
                            final double dz = p1.$object().z - p2.$object().z;
                            final double d2 = ((dx * dx) + (dy * dy)) + (dz * dz);
                            final double mag = Constants.INSTANCE.dt / d2;
                            v1.$object().vx -= (dx * p2.$object().mass) * mag;
                            v1.$object().vy -= (dy * p2.$object().mass) * mag;
                            v1.$object().vz -= (dz * p2.$object().mass) * mag;
                        }
                    }
                }
                return null;
            }
        };
    }
    
    private Task<Void> updatePosition$Task(final GuardedSlice<BodyWithFields[]> bodies) {
        return new Task<Void>(new Object[]{bodies}, new Object[]{}) {
            @Override
            protected Void runRolez() {
                final long $task = idBits();
                for(int i = bodies.range.begin; i < bodies.range.end; i += bodies.range.step) {
                    final BodyWithFields body = bodies.get(i);
                    body.x += Constants.INSTANCE.dt * body.vx;
                    body.y += Constants.INSTANCE.dt * body.vy;
                    body.z += Constants.INSTANCE.dt * body.vz;
                }
                return null;
            }
        };
    }
    
    private Task<Void> main$Task() {
        return new Task<Void>(new Object[]{this}, new Object[]{}) {
            @Override
            protected Void runRolez() {
                final long $task = idBits();
                NBodyRolezLazySlicing.this.out = System.out;
                NBodyRolezLazySlicing.this.createSystem(new Random(), $task);
                System.out.println("Warming up...");
                NBodyRolezLazySlicing.this.simulate($task);
                System.out.println("Press Enter to start");
                new java.util.Scanner(System.in).nextLine();
                NBodyRolezLazySlicing.this.simulate($task);
                return null;
            }
        };
    }
    
    public static void main(final String[] args) {
        TaskSystem.getDefault().run(new NBodyRolezLazySlicing(0L).main$Task());
    }
    
    @Override
    protected List<?> guardedRefs() {
        return asList(system);
    }
}
