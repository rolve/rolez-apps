package ch.trick17.rolezapps.idea;

import java.util.Scanner;

import rolez.lang.GuardedSlice;
import rolez.lang.Task;
import rolez.util.StopWatch;

public class IdeaEncryptionRolezArrayOpt extends IdeaEncryptionRolez {
    
    public IdeaEncryptionRolezArrayOpt(long $task) {
        super($task);
    }
    
    public IdeaEncryptionRolezArrayOpt(int size, int tasks, long $task) {
        super(size, tasks, $task);
    }

    @Override
    public void encryptDecrypt(final rolez.lang.GuardedSlice<byte[]> src,
            final rolez.lang.GuardedSlice<byte[]> dst, final int[] key, final long $task) {
        final int xFF = 255;
        final int xFFFF = 65535;
        final long x10001L = 65537L;
        
        byte[] srcData = src.data;
        byte[] dstData = dst.data;
        
        int iSrc = src.range.begin;
        int iDst = src.range.begin;
        for(int i = src.range.begin; i < src.range.end; i += 8) {
            guardReadOnly(src, $task);
            int x1 = srcData[iSrc++] & xFF;
            x1 = x1 | ((srcData[iSrc++] & xFF) << 8);
            int x2 = srcData[iSrc++] & xFF;
            x2 = x2 | ((srcData[iSrc++] & xFF) << 8);
            int x3 = srcData[iSrc++] & xFF;
            x3 = x3 | ((srcData[iSrc++] & xFF) << 8);
            int x4 = srcData[iSrc++] & xFF;
            x4 = x4 | ((srcData[iSrc++] & xFF) << 8);
            int iKey = 0;
            for(int round = 0; round < 8; round++) {
                x1 = (int) (((((long) x1) * key[iKey++]) % x10001L) & xFFFF);
                x2 = (x2 + key[iKey++]) & xFFFF;
                x3 = (x3 + key[iKey++]) & xFFFF;
                x4 = (int) (((((long) x4) * key[iKey++]) % x10001L) & xFFFF);
                int t0 = x1 ^ x3;
                t0 = (int) (((((long) t0) * key[iKey++]) % x10001L) & xFFFF);
                int t1 = (t0 + (x2 ^ x4)) & xFFFF;
                t1 = (int) (((((long) t1) * key[iKey++]) % x10001L) & xFFFF);
                t0 = (t1 + t0) & xFFFF;
                x1 = x1 ^ t1;
                x4 = x4 ^ t0;
                t0 = t0 ^ x2;
                x2 = x3 ^ t1;
                x3 = t0;
            }
            x1 = (int) (((((long) x1) * key[iKey++]) % x10001L) & xFFFF);
            x3 = (x3 + key[iKey++]) & xFFFF;
            x2 = (x2 + key[iKey++]) & xFFFF;
            x4 = (int) (((((long) x4) * key[iKey++]) % x10001L) & xFFFF);
            guardReadWrite(dst, $task);
            dstData[iDst++] = (byte) x1;
            dstData[iDst++] = (byte) (x1 >>> 8);
            dstData[iDst++] = (byte) x3;
            dstData[iDst++] = (byte) (x3 >>> 8);
            dstData[iDst++] = (byte) x2;
            dstData[iDst++] = (byte) (x2 >>> 8);
            dstData[iDst++] = (byte) x4;
            dstData[iDst++] = (byte) (x4 >>> 8);
        }
    }
    
    @Override
    public Task<Void> encryptDecrypt$Task(final GuardedSlice<byte[]> src,
            final GuardedSlice<byte[]> dst, final int[] key) {
        return new Task<Void>(new Object[]{dst}, new Object[]{src}) {
            @Override
            protected Void runRolez() {
                int xFF = 255;
                int xFFFF = 65535;
                long x10001L = 65537L;
                
                byte[] srcData = src.data;
                byte[] dstData = dst.data;
                
                int iSrc = src.range.begin;
                int iDst = src.range.begin;
                for(int i = src.range.begin; i < src.range.end; i += 8) {
                    int x1 = srcData[iSrc++] & xFF;
                    x1 = x1 | ((srcData[iSrc++] & xFF) << 8);
                    int x2 = srcData[iSrc++] & xFF;
                    x2 = x2 | ((srcData[iSrc++] & xFF) << 8);
                    int x3 = srcData[iSrc++] & xFF;
                    x3 = x3 | ((srcData[iSrc++] & xFF) << 8);
                    int x4 = srcData[iSrc++] & xFF;
                    x4 = x4 | ((srcData[iSrc++] & xFF) << 8);
                    int iKey = 0;
                    for(int round = 0; round < 8; round++) {
                        x1 = (int) (((((long) x1) * key[iKey++]) % x10001L) & xFFFF);
                        x2 = (x2 + key[iKey++]) & xFFFF;
                        x3 = (x3 + key[iKey++]) & xFFFF;
                        x4 = (int) (((((long) x4) * key[iKey++]) % x10001L) & xFFFF);
                        int t0 = x1 ^ x3;
                        t0 = (int) (((((long) t0) * key[iKey++]) % x10001L) & xFFFF);
                        int t1 = (t0 + (x2 ^ x4)) & xFFFF;
                        t1 = (int) (((((long) t1) * key[iKey++]) % x10001L) & xFFFF);
                        t0 = (t1 + t0) & xFFFF;
                        x1 = x1 ^ t1;
                        x4 = x4 ^ t0;
                        t0 = t0 ^ x2;
                        x2 = x3 ^ t1;
                        x3 = t0;
                    }
                    x1 = (int) (((((long) x1) * key[iKey++]) % x10001L) & xFFFF);
                    x3 = (x3 + key[iKey++]) & xFFFF;
                    x2 = (x2 + key[iKey++]) & xFFFF;
                    x4 = (int) (((((long) x4) * key[iKey++]) % x10001L) & xFFFF);
                    dstData[iDst++] = (byte) x1;
                    dstData[iDst++] = (byte) (x1 >>> 8);
                    dstData[iDst++] = (byte) x3;
                    dstData[iDst++] = (byte) (x3 >>> 8);
                    dstData[iDst++] = (byte) x2;
                    dstData[iDst++] = (byte) (x2 >>> 8);
                    dstData[iDst++] = (byte) x4;
                    dstData[iDst++] = (byte) (x4 >>> 8);
                }
                return null;
            }
        };
    }
    
    @Override
    public Task<Void> main$Task() {
        return new rolez.lang.Task<Void>(new Object[]{this}, new Object[]{}) {
            @Override
            protected java.lang.Void runRolez() {
                long $task = idBits();
                buildTestData(new java.util.Random(42L), $task);
                
                System.out.println("Press Enter to start");
                new Scanner(System.in).nextLine();
                
                StopWatch watch = new StopWatch();
                for(int i = 0; i < 20; i++) {
                    watch.go();
                    IdeaEncryptionRolezArrayOpt.this.run($task);
                    System.out.println(watch.get());
                }
                return null;
            }
        };
    }
    
    public static void main(final java.lang.String[] args) {
        rolez.lang.TaskSystem.getDefault().run(new IdeaEncryptionRolezArrayOpt(0L).main$Task());
    }
}
