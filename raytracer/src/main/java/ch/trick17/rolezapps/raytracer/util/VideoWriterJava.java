package ch.trick17.rolezapps.raytracer.util;

import static ch.trick17.rolezapps.raytracer.util.ImageWriterJava.toBufferedImage;
import static org.jcodec.scale.AWTUtil.fromBufferedImage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.encode.RateControl;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

import rolez.lang.Guarded;

/**
 * This class is derived from the {@link org.jcodec.api.SequenceEncoder} class from JCodec
 * (www.jcodec.org), which is licensed under the FreeBSD license.
 * 
 * @author The JCodec project
 * @author Michael Faes
 */
public class VideoWriterJava extends Guarded {

    public final int width;
    public final int height;
    public final int framerate;
    
    private final SeekableByteChannel channel;
    private final Transform transform;
    private final H264Encoder encoder;
    private final ArrayList<ByteBuffer> spsList;
    private final ArrayList<ByteBuffer> ppsList;
    private final FramesMP4MuxerTrack track;
    private final ByteBuffer buffer;
    private final MP4Muxer muxer;
    
    private int frameNo;
    
    public VideoWriterJava(String file, int width, int height, int framerate, int qp)
            throws IOException {
        this.width = width;
        this.height = height;
        this.framerate = framerate;
        
        channel = NIOUtils.writableFileChannel(new File(file));
        
        // Muxer that will store the encoded frames
        muxer = new MP4Muxer(channel, Brand.MP4);
        
        // Add video track to muxer
        track = muxer.addTrack(TrackType.VIDEO, framerate);
        
        // Allocate a buffer big enough to hold output frames
        buffer = ByteBuffer.allocate(width * height * 6);
        
        // Create an instance of encoder
        encoder = new H264Encoder(new FixedQp(qp));
        
        // Transform to convert between RGB and YUV
        transform = ColorUtil.getTransform(ColorSpace.RGB, encoder.getSupportedColorSpaces()[0]);
        
        // Encoder extra data ( SPS, PPS ) to be stored in a special place of MP4
        spsList = new ArrayList<ByteBuffer>();
        ppsList = new ArrayList<ByteBuffer>();
    }
    
    public void writeFrame(int[][] imageData) throws IOException {
        if(!channel.isOpen())
            throw new IllegalStateException("already closed");
        if(imageData.length != height || imageData[0].length != width)
            throw new IllegalArgumentException("invalid dimensions");
        
        Picture pic = fromBufferedImage(toBufferedImage(imageData));
        Picture transformed = Picture.create(width, height, encoder
                .getSupportedColorSpaces()[0]);
        
        // Perform conversion
        transform.transform(pic, transformed);
        
        // Encode image into H.264 frame, the result is stored in buffer
        buffer.clear();
        ByteBuffer res = encoder.encodeFrame(transformed, buffer);
        
        // Based on the frame above form correct MP4 packet
        spsList.clear();
        ppsList.clear();
        H264Utils.wipePS(res, spsList, ppsList);
        H264Utils.encodeMOVPacket(res);
        
        // Add packet to video track
        track.addFrame(new MP4Packet(res, frameNo, framerate, 1, frameNo, true, null, frameNo, 0));
        
        frameNo++;
    }
    
    public void close() throws IOException {
        if(!channel.isOpen())
            throw new IllegalStateException("already closed");
        
        // Push saved SPS/PPS to a special storage in MP4
        track.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList, 4));
        
        // Write MP4 header and finalize recording
        muxer.writeHeader();
        NIOUtils.closeQuietly(channel);
    }
    
    private static class FixedQp implements RateControl {
        
        private final int qp;
        
        public FixedQp(int qp) {
            this.qp = qp;
        }
        
        public int getInitQp() {
            return qp;
        }
        
        public int getQpDelta() {
            return 0;
        }
        
        public boolean accept(int bits) {
            return true;
        }
        
        public void reset() {}
    }
}