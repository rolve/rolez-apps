package ch.trick17.rolezapps.raytracer.util;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageWriterJava {
    
    public static void write(int[][] imageData, String formatName, String file) throws IOException {
        ImageIO.write(toBufferedImage(imageData), formatName, new File(file));
    }
    
    public static BufferedImage toBufferedImage(int[][] imageData) {
        int width = imageData[0].length;
        int height = imageData.length;
        BufferedImage image = new BufferedImage(width, height, TYPE_INT_RGB);
        
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                image.setRGB(x, y, imageData[y][x]);
        return image;
    }
}
