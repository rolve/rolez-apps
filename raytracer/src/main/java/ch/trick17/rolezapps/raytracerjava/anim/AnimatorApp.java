package ch.trick17.rolezapps.raytracerjava.anim;

import static java.lang.Math.PI;

import java.io.IOException;
import java.util.Random;

import ch.trick17.rolezapps.raytracer.Color;
import ch.trick17.rolezapps.raytracer.Colors;
import ch.trick17.rolezapps.raytracer.anim.Duration;
import ch.trick17.rolezapps.raytracer.anim.WholeTime;
import ch.trick17.rolezapps.raytracer.util.VideoWriterJava;
import ch.trick17.rolezapps.raytracerjava.Light;
import ch.trick17.rolezapps.raytracerjava.Material;
import ch.trick17.rolezapps.raytracerjava.Plane;
import ch.trick17.rolezapps.raytracerjava.Raytracer;
import ch.trick17.rolezapps.raytracerjava.Sphere;
import ch.trick17.rolezapps.raytracerjava.Vector3D;
import ch.trick17.rolezapps.raytracerjava.View;

public final class AnimatorApp {
    
    public static void main(String[] args) throws IOException {
        Raytracer raytracer = new Raytracer();
        raytracer.numTasks = 8;
        raytracer.oversample = 2;
        
        AnimatedScene scene = new AnimatedScene(30.0);
        buildScene(scene, new Random());
        int width = 800;
        int height = (int) (width / scene.view.aspect);
        VideoWriterJava writer = new VideoWriterJava("movie.mp4", width, height, 25, 12);
        
        Animator animator = new Animator(raytracer, scene, writer);
        animator.printProgress = true;
        animator.render();
    }
    
    public static void buildScene(AnimatedScene scene, Random random) {
        double height = 22.0;
        double side = 40.0;
        double camDist = (side / 2) - 1;
        int ballsPerS = 4;
        
        double dimLength = 11.0;
        double darkLength = 1.0;
        
        View view = new View();
        view.from = new Vector3D(-camDist, 0.0, (height / 2) - 1);
        view.at = new Vector3D(0.0, 0.0, 0.0);
        view.up = new Vector3D(0.0, 0.0, 1.0);
        view.distance = 1.0;
        view.angle = Math.toRadians(35.0);
        view.aspect = 16.0 / 9.0;
        scene.view = view;
        scene.animations.add(new CircularMovement(WholeTime.INSTANCE, view, new Vector3D(0.0, 0.0,
                (height / 2) - 1).minus(view.from), view.up, Math.PI / 6));
        scene.animations.add(new LinearMovement(WholeTime.INSTANCE, view, new Vector3D(0.0, 0.0,
                (-(height - 2)) / scene.length)));
        
        Material red = new Material();
        red.color = new Color(0.9, 0.1, 0.1, null);
        red.kd = 0.9;
        red.shine = 15.0;
        red.ks = 0.5;
        
        Material white = new Material();
        white.color = new Color(0.9, 0.9, 1.0, null);
        white.kd = 1.2;
        white.shine = 100.0;
        white.ks = 0.25;
        
        Material glass = new Material();
        glass.color = Colors.INSTANCE.black;
        glass.kd = 0.0;
        glass.shine = 15.0;
        glass.ks = 0.2;
        glass.kt = 1.0;
        glass.ior = 1.05;
        
        Material blue = new Material();
        blue.color = new Color(0.0, 0.0, 0.5, null);
        blue.kd = 0.5;
        blue.shine = 10.0;
        blue.ks = 0.2;
        
        Material black = new Material();
        black.color = Colors.INSTANCE.black;
        black.kd = 0.0;
        black.shine = 10.0;
        black.ks = 0.2;
        
        Plane wall1 = new Plane(new Vector3D(-1.0, 0.0, 0.0), side / 2, white);
        Plane wall2 = new Plane(new Vector3D(1.0, 0.0, 0.0), side / 2, white);
        Plane wall3 = new Plane(new Vector3D(0.0, -1.0, 0.0), side / 2, white);
        Plane wall4 = new Plane(new Vector3D(0.0, 1.0, 0.0), side / 2, white);
        Plane ceil = new Plane(new Vector3D(0.0, 0.0, -1.0), height / 2, blue);
        Plane floor = new Plane(new Vector3D(0.0, 0.0, 1.0), height / 2, black);
        scene.objects.add(wall1);
        scene.objects.add(wall2);
        scene.objects.add(wall3);
        scene.objects.add(wall4);
        scene.objects.add(floor);
        scene.objects.add(ceil);
        
        Light mainLight = new Light(new Vector3D(0.0, 0.0, 0.0), 5.0);
        scene.lights.add(mainLight);
        scene.animations.add(new BrightnessChange(new Duration((scene.length - dimLength)
                - darkLength, null), mainLight, (-mainLight.brightness) / (0.5 * dimLength)));
        
        scene.ambientLight = 0.2;
        scene.animations.add(new AmbientLightChange(new Duration((scene.length - (0.75 * dimLength))
                - darkLength, null), scene, (-scene.ambientLight) / (0.5 * dimLength)));
        
        Duration lilLightsDimDuration = new Duration((scene.length - (0.5 * dimLength))
                - darkLength, null);
        for(double t = 0.0; t < scene.length; t += 1.0 / ballsPerS) {
            double r = Math.abs(1.5 + (0.75 * random.nextGaussian()));
            
            double dist = (r + 0.1) + (((camDist - (2 * r)) - 0.2) * random.nextDouble());
            double angle = (2 * PI) * random.nextDouble();
            double x = dist * Math.cos(angle);
            double y = dist * Math.sin(angle);
            
            double speed = Math.abs(6 + (3 * random.nextGaussian()));
            Duration duration = new Duration(t, t + ((height + (2 * r)) / speed), null);
            
            Sphere ball = new Sphere(new Vector3D(x, y, ((-height) / 2) - r), r, red);
            Light light = new Light(new Vector3D(x, y + (1.2 * r), ((-height) / 2) - r), 0.2);
            
            scene.animations.add(new BallAnimation(duration, ball, speed, scene));
            scene.animations.add(new LightAnimation(duration, light, ball.center, speed, PI,
                    scene));
            scene.animations.add(new BrightnessChange(lilLightsDimDuration, light,
                    (-light.brightness) / (0.5 * dimLength)));
        }
    }
}
