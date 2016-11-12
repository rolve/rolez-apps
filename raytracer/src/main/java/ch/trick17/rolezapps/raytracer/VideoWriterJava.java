package ch.trick17.rolezapps.raytracer;

import java.io.File;
import java.io.IOException;

import org.jcodec.api.awt.SequenceEncoder;

public class VideoWriterJava {
    
    private final SequenceEncoder encoder;
    private boolean closed;
    
    public VideoWriterJava(String file) throws IOException {
        encoder = new SequenceEncoder(new File(file));
    }
    
    public void writeFrame(int[][] imageData) throws IOException {
        if(closed)
            throw new IllegalStateException("already closed");
        encoder.encodeImage(ImageWriterJava.toBufferedImage(imageData));
    }
    
    public void close() throws IOException {
        if(closed)
            throw new IllegalStateException("already closed");
        encoder.finish();
        closed = true;
    }
}