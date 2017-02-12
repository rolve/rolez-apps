package ch.trick17.rolezapps.raytracer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PlaneTest {
    
    @Test
    public void testIntersect() {
        Plane plane = new Plane(new Vector3D(0, 0, 1, 0L), 1, new Material(0L), 0L);
        Intersection i = plane.intersect(new Ray(new Vector3D(0, 0, 0, 0L),
                new Vector3D(0, 0, -1, 0L), 0L), 0L);
        assertEquals(1, i.t, Double.MIN_NORMAL);
        assertTrue(i.enter);
        assertSame(plane, i.primitive);
    }
}
