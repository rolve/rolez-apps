package ch.trick17.rolezapps.raytracerjava;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import ch.trick17.rolezapps.raytracer.Color;
import ch.trick17.rolezapps.raytracer.Colors;
import rolez.lang.SliceRange;
import rolez.lang.StripedPartitioner;

public class Raytracer {
    
    public int numTasks = 1;
    public int maxRecursions = 10;
    public int oversample = 1;
    public boolean renderLights = true;
    public Scene scene;
    
    public void render(int[][] image) {
        SliceRange fullRange = new SliceRange(0, image.length, 1);
        SliceRange[] ranges = StripedPartitioner.INSTANCE.partition(fullRange, numTasks).data;
        
        List<FutureTask<Void>> tasks = new ArrayList<>();
        for(int i = 0; i < ranges.length - 1; i += 1) {
            FutureTask<Void> task = $renderPartTask(image, ranges[i]);
            tasks.add(task);
            new Thread(task).start();
        }
        
        renderPart(image, ranges[ranges.length - 1]);
        
        for(FutureTask<Void> task : tasks)
            try {
                task.get();
            } catch(InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
    }
    
    public void renderPart(int[][] image, SliceRange range) {
        View view = scene.view;
        int width = image[range.begin].length;
        int height = image.length;
        double frustrumHeight = view.distance * Math.tan(view.angle);
        Vector3D viewDir = view.at.minus(view.from).normalize();
        Vector3D viewLeft = view.up.cross(viewDir).normalize().scale(view.aspect * frustrumHeight);
        Vector3D viewDown = view.up.minus(viewDir.scale(view.up.dot(viewDir))).normalize()
                .scale(-frustrumHeight);
        for(int y = range.begin; y < range.end; y += range.step)
            for(int x = 0; x < width; x += 1) {
                Color color = this.renderPixel(x, y, width, height, viewDir, viewLeft, viewDown);
                image[y][x] = color.toRgbInt(0L);
            }
    }
    
    public FutureTask<Void> $renderPartTask(final int[][] image, final SliceRange range) {
        return new FutureTask<Void>(new Runnable() {
            public void run() {
                renderPart(image, range);
            }
        }, null);
    }
    
    public Color renderPixel(int x, int y, int width, int height, Vector3D viewDir,
            Vector3D viewLeft, Vector3D viewDown) {
        int samples = oversample;
        ColorBuilder color = new ColorBuilder();
        for(int ySample = 0; ySample < samples; ySample += 1)
            for(int xSample = 0; xSample < samples; xSample += 1) {
                double xLen = ((2.0 * (x + (((double) xSample) / samples))) / width) - 1;
                double yLen = ((2.0 * (y + (((double) ySample) / samples))) / height) - 1;
                Vector3D dir = viewDir.plus(viewLeft.scale(xLen)).plus(viewDown.scale(yLen));
                Ray ray = new Ray(scene.view.from, dir.normalize());
                color.add(trace(ray, 1.0, 0));
                if(renderLights)
                    for(int i = 0; i < scene.lights.size(); i += 1)
                        renderLight(ray, scene.lights.get(i), color);
            }
        color.multiply(1.0 / (samples * samples));
        return color.build();
    }
    
    public Color trace(Ray ray, double intensity, int recursion) {
        if(recursion == maxRecursions)
            return Colors.INSTANCE.black;
        
        Intersection intersection = scene.intersectJava(ray);
        if(intersection != null)
            return shade(intersection, ray, intensity, recursion);
        else
            return scene.background;
    }
    
    public Color shade(Intersection intersection, Ray ray, double intensity, int recursion) {
        Material mat = intersection.primitive.mat;
        Vector3D point = ray.point(intersection.t);
        Vector3D normal = intersection.primitive.normal(point);
        if(ray.direction.dot(normal) > 0)
            normal = normal.negate();
        
        Vector3D reflectDir = ray.direction.reflect(normal).normalize();
        ColorBuilder color = new ColorBuilder();
        color.add(mat.color, scene.ambientLight);
        for(int i = 0; i < scene.lights.size(); i += 1) {
            Light light = scene.lights.get(i);
            Vector3D lightVec = light.position.minus(point);
            if(normal.dot(lightVec) >= 0) {
                Ray shadowRay = new Ray(point, lightVec);
                Intersection intersect = scene.intersectJava(shadowRay);
                if((intersect == null) || (intersect.t > lightVec.length()))
                    light(mat, light, lightVec, normal, reflectDir, color);
            }
        }
        if((mat.ks * intensity) > 0.001)
            color.add(trace(new Ray(point, reflectDir), intensity * mat.ks, recursion + 1), mat.ks);
        
        if((mat.kt * intensity) > 0.001) {
            double eta;
            if(intersection.enter)
                eta = 1 / mat.ior;
            else
                eta = mat.ior;
            
            Ray refractRay = new Ray(point, ray.direction.refract(normal, eta));
            color.add(trace(refractRay, intensity * mat.kt, recursion + 1), mat.kt);
        }
        
        return color.build();
    }
    
    public void light(Material mat, Light light, Vector3D lightVec, Vector3D normal,
            Vector3D reflectDir, ColorBuilder color) {
        Vector3D lightDir = lightVec.normalize();
        double intensity = light.brightness / lightVec.length();
        double shade = (intensity * mat.kd) * normal.dot(lightDir);
        color.add(mat.color, shade);
        if(mat.shine > 0.000001) {
            double spec = reflectDir.dot(lightDir);
            if(spec > 0.000001)
                color.add(Math.pow(spec, mat.shine / intensity));
        }
    }
    
    public void renderLight(Ray ray, Light light, ColorBuilder color) {
        Vector3D lightVec = light.position.minus(ray.origin);
        Intersection intersect = scene.intersectJava(new Ray(ray.origin, lightVec));
        if((intersect == null) || (intersect.t > lightVec.length())) {
            double intensity = light.brightness / lightVec.length();
            double spec = ray.direction.dot(lightVec.normalize());
            if(spec > 0.000001)
                color.add(Math.pow(spec, 50 / intensity));
        }
    }
}
