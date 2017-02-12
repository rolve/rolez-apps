package ch.trick17.rolezapps.raytracer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PlaneTest {
    
    @Test
    public void testIntersect() {
        Plane plane = new Plane(new Vector3D(0, 0, 1, null), 1, new Material(null), null);
        Intersection i = plane.intersect(new Ray(new Vector3D(0, 0, 0, null),
                new Vector3D(0, 0, -1, null), null), null);
        assertEquals(1, i.t, Double.MIN_NORMAL);
        assertTrue(i.enter);
        assertSame(plane, i.primitive);
    }
}
