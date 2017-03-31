package ch.trick17.rolezapps.histogram.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import rolez.lang.GuardedArray;

public class ImageReaderJava {
    
    public static GuardedArray<GuardedArray<int[]>[]> read(String file) throws IOException {
        return fromBufferedImage(ImageIO.read(new File(file)));
    }
    
    public static GuardedArray<GuardedArray<int[]>[]> fromBufferedImage(BufferedImage image) {
        @SuppressWarnings("unchecked")
        GuardedArray<GuardedArray<int[]>[]> result = 
                new GuardedArray<GuardedArray<int[]>[]>(new GuardedArray[image.getHeight()]);
        for(int y = 0; y < image.getHeight(); y++) {
            GuardedArray<int[]> row = new GuardedArray<int[]>(new int[image.getWidth()]);
            for(int x = 0; x < image.getWidth(); x++)
                row.data[x] = image.getRGB(x, y);
            result.data[y] = row;
        }
        return result;
    }
}
