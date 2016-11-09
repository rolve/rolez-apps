package ch.trick17.rolezapps.raytracer;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageWriterJava {
    
    public static void write(int[][] imageData, String formatName, String file) {
        int width = imageData[0].length;
        int height = imageData.length;
        BufferedImage image = new BufferedImage(width, height, TYPE_INT_RGB);
        
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                image.setRGB(x, y, imageData[y][x]);
        
        try {
            ImageIO.write(image, formatName, new File(file));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
