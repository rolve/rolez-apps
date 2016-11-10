package ch.trick17.rolezapps.raytracer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Vector3DTest {
    
    private static final double eps = 1e-6;
    
    @Test
    public void testRefract() {
        Vector3D v = new Vector3D(1, 0, 0);
        
        Vector3D r = v.refract(new Vector3D(-1, 0, 0), 1);
        assertEquals(1, r.x, eps);
        assertEquals(0, r.y, eps);
        assertEquals(0, r.z, eps);
        
        r = v.refract(new Vector3D(1, -1, 0).normalize(), 1);
        assertEquals(0, r.x, eps);
        assertEquals(1, r.y, eps);
        assertEquals(0, r.z, eps);
    }
}