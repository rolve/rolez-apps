package ch.trick17.rolezapps.idea;

import java.util.Arrays;
import java.util.Random;

/**
 * This test performs IDEA encryption then decryption. IDEA stands for International Data Encryption
 * Algorithm. The test is based on code presented in Applied Cryptography by Bruce Schnier, which
 * was based on code developed by Xuejia Lai and James L. Massey.
 */
public class IdeaEncyption {
    
    // Declare class data. Byte buffer plain1 holds the original
    // data for encryption, crypt1 holds the encrypted data, and
    // plain2 holds the decrypted data, which should match plain1
    // byte for byte.
    
    private final int size;
    private final int threads;
    
    private byte[] plain1; // Buffer for plaintext data.
    private byte[] crypt1; // Buffer for encrypted data.
    private byte[] plain2; // Buffer for decrypted data.
    
    private short[] userkey; // Key for encryption/decryption.
    private int[] Z;         // Encryption subkey (userkey derived).
    private int[] DK;        // Decryption subkey (userkey derived).
    
    
    public IdeaEncyption(int size, int threads) {
        this.size = size;
        this.threads = threads;
    }
    
    /**
     * Builds the data used for the test
     */
    public void buildTestData() {
        
        // Create three byte arrays that will be used (and reused) for
        // encryption/decryption operations.
        
        plain1 = new byte[size];
        crypt1 = new byte[size];
        plain2 = new byte[size];
        
        Random random = new Random(136506717L); // Create random number generator.
        
        // Allocate three arrays to hold keys: userkey is the 128-bit key.
        // Z is the set of 16-bit encryption subkeys derived from userkey,
        // while DK is the set of 16-bit decryption subkeys also derived
        // from userkey. NOTE: The 16-bit values are stored here in
        // 32-bit int arrays so that the values may be used in calculations
        // as if they are unsigned. Each 64-bit block of plaintext goes
        // through eight processing rounds involving six of the subkeys
        // then a final output transform with four of the keys; (8 * 6)
        // + 4 = 52 subkeys.
        
        userkey = new short[8]; // User key has 8 16-bit shorts.
        Z = new int[52]; // Encryption subkey (user key derived).
        DK = new int[52]; // Decryption subkey (user key derived).
        
        // Generate user key randomly; eight 16-bit values in an array.
        
        for(int i = 0; i < 8; i++) {
            // Again, the random number function returns int. Converting
            // to a short type preserves the bit pattern in the lower 16
            // bits of the int and discards the rest.
            
            userkey[i] = (short) random.nextInt();
        }
        
        // Compute encryption and decryption subkeys.
        
        calcEncryptKey();
        calcDecryptKey();
        
        // Fill plain1 with "text."
        for(int i = 0; i < size; i++) {
            plain1[i] = (byte) i;
            
            // Converting to a byte
            // type preserves the bit pattern in the lower 8 bits of the
            // int and discards the rest.
        }
    }
    
    /**
     * Builds the 52 16-bit encryption subkeys Z[] from the user key and stores in 32-bit int array.
     * The routing corrects an error in the source code in the Schnier book. Basically, the sense of
     * the 7- and 9-bit shifts are reversed. It still works reversed, but would encrypted code would
     * not decrypt with someone else's IDEA code.
     */
    private void calcEncryptKey() {
        for(int i = 0; i < 52; i++) // Zero out the 52-int Z array.
            Z[i] = 0;
        
        for(int i = 0; i < 8; i++) // First 8 subkeys are userkey itself.
            Z[i] = userkey[i] & 0xffff; // Convert "unsigned"
                                        // short to int.
        
        // Each set of 8 subkeys thereafter is derived from left rotating
        // the whole 128-bit key 25 bits to left (once between each set of
        // eight keys and then before the last four). Instead of actually
        // rotating the whole key, this routine just grabs the 16 bits
        // that are 25 bits to the right of the corresponding subkey
        // eight positions below the current subkey. That 16-bit extent
        // straddles two array members, so bits are shifted left in one
        // member and right (with zero fill) in the other. For the last
        // two subkeys in any group of eight, those 16 bits start to
        // wrap around to the first two members of the previous eight.
        
        for(int i = 8; i < 52; i++) {
            int j = i % 8;
            if(j < 6) {
                Z[i] = ((Z[i - 7] >>> 9) | (Z[i - 6] << 7)) & 0xFFFF; // Shift and combine. Just 16 bits.
                continue; // Next iteration.
            }
            
            if(j == 6) { // Wrap to beginning for second chunk.
                Z[i] = ((Z[i - 7] >>> 9) | (Z[i - 14] << 7)) & 0xFFFF;
                continue;
            }
            
            // j == 7 so wrap to beginning for both chunks.
            Z[i] = ((Z[i - 15] >>> 9) | (Z[i - 14] << 7)) & 0xFFFF;
        }
    }
    
    /**
     * Builds the 52 16-bit encryption subkeys DK[] from the encryption- subkeys Z[]. DK[] is a
     * 32-bit int array holding 16-bit values as unsigned.
     */
    private void calcDecryptKey() {
        int j, k; // Index counters.
        int t1, t2, t3; // Temps to hold decrypt subkeys.
        
        t1 = inv(Z[0]); // Multiplicative inverse (mod x10001).
        t2 = -Z[1] & 0xffff; // Additive inverse, 2nd encrypt subkey.
        t3 = -Z[2] & 0xffff; // Additive inverse, 3rd encrypt subkey.
        
        DK[51] = inv(Z[3]); // Multiplicative inverse (mod x10001).
        DK[50] = t3;
        DK[49] = t2;
        DK[48] = t1;
        
        j = 47; // Indices into temp and encrypt arrays.
        k = 4;
        for(int i = 0; i < 7; i++) {
            t1 = Z[k++];
            DK[j--] = Z[k++];
            DK[j--] = t1;
            t1 = inv(Z[k++]);
            t2 = -Z[k++] & 0xffff;
            t3 = -Z[k++] & 0xffff;
            DK[j--] = inv(Z[k++]);
            DK[j--] = t2;
            DK[j--] = t3;
            DK[j--] = t1;
        }
        
        t1 = Z[k++];
        DK[j--] = Z[k++];
        DK[j--] = t1;
        t1 = inv(Z[k++]);
        t2 = -Z[k++] & 0xffff;
        t3 = -Z[k++] & 0xffff;
        DK[j--] = inv(Z[k++]);
        DK[j--] = t3;
        DK[j--] = t2;
        DK[j--] = t1;
    }
    
    public void run() {
        Runnable thobjects[] = new Runnable[threads];
        Thread th[] = new Thread[threads];
        
        // Encrypt plain1.
        for(int i = 1; i < threads; i++) {
            thobjects[i] = new IDEARunner(i, plain1, crypt1, Z, threads);
            th[i] = new Thread(thobjects[i]);
            th[i].start();
        }
        
        thobjects[0] = new IDEARunner(0, plain1, crypt1, Z, threads);
        thobjects[0].run();
        
        for(int i = 1; i < threads; i++) {
            try {
                th[i].join();
            } catch(InterruptedException e) {}
        }
        
        // Decrypt.
        for(int i = 1; i < threads; i++) {
            thobjects[i] = new IDEARunner(i, crypt1, plain2, DK, threads);
            th[i] = new Thread(thobjects[i]);
            th[i].start();
        }
        
        thobjects[0] = new IDEARunner(0, crypt1, plain2, DK, threads);
        thobjects[0].run();
        
        for(int i = 1; i < threads; i++) {
            try {
                th[i].join();
            } catch(InterruptedException e) {}
        }
    }
    
    /**
     * Compute multiplicative inverse of x, modulo (2**16)+1 using extended Euclid's GCD (greatest
     * common divisor) algorithm. It is unrolled twice to avoid swapping the meaning of the
     * registers. And some subtracts are changed to adds. Java: Though it uses signed 32-bit ints,
     * the interpretation of the bits within is strictly unsigned 16-bit.
     */
    private static int inv(int x) {
        int t0, t1;
        int q, y;
        
        if(x <= 1) // Assumes positive x.
            return(x); // 0 and 1 are self-inverse.
            
        t1 = 0x10001 / x; // (2**16+1)/x; x is >= 2, so fits 16 bits.
        y = 0x10001 % x;
        if(y == 1)
            return((1 - t1) & 0xFFFF);
        
        t0 = 1;
        do {
            q = x / y;
            x = x % y;
            t0 += q * t1;
            if(x == 1)
                return(t0);
            q = y / x;
            y = y % x;
            t1 += q * t0;
        } while(y != 1);
        
        return((1 - t1) & 0xFFFF);
    }
    
    public void validate() {
        if(!Arrays.equals(plain1, plain2))
            throw new AssertionError("Validation failed");
    }
}

class IDEARunner implements Runnable {
    
    int id, key[];
    byte text1[], text2[];
    private final int threads;
    
    public IDEARunner(int id, byte[] text1, byte[] text2, int[] key, int threads) {
        this.id = id;
        this.text1 = text1;
        this.text2 = text2;
        this.key = key;
        this.threads = threads;
    }
    
    /**
     * IDEA encryption/decryption algorithm. It processes plaintext in 64-bit blocks, one at a time,
     * breaking the block into four 16-bit unsigned subblocks. It goes through eight rounds of
     * processing using 6 new subkeys each time, plus four for last step. The source text is in
     * array text1, the destination text goes into array text2 The routine represents 16-bit
     * subblocks and subkeys as type int so that they can be treated more easily as unsigned.
     * Multiplication modulo 0x10001 interprets a zero sub-block as 0x10000; it must to fit in 16
     * bits.
     */
    public void run() {
        int ilow, iupper, slice, tslice, ttslice;
        
        tslice = text1.length / 8;
        ttslice = (tslice + threads - 1) / threads;
        slice = ttslice * 8;
        
        ilow = id * slice;
        iupper = (id + 1) * slice;
        if(iupper > text1.length)
            iupper = text1.length;
        
        int i1 = ilow; // Index into first text array.
        int i2 = ilow; // Index into second text array.
        int ik; // Index into key array.
        int x1, x2, x3, x4, t1, t2; // Four "16-bit" blocks, two temps.
        int r; // Eight rounds of processing.
        
        for(int i = ilow; i < iupper; i += 8) {
            ik = 0; // Restart key index.
            r = 8; // Eight rounds of processing.
            
            // Load eight plain1 bytes as four 16-bit "unsigned" integers.
            // Masking with 0xff prevents sign extension with cast to int.
            
            x1 = text1[i1++] & 0xff; // Build 16-bit x1 from 2 bytes,
            x1 |= (text1[i1++] & 0xff) << 8; // assuming low-order byte first.
            x2 = text1[i1++] & 0xff;
            x2 |= (text1[i1++] & 0xff) << 8;
            x3 = text1[i1++] & 0xff;
            x3 |= (text1[i1++] & 0xff) << 8;
            x4 = text1[i1++] & 0xff;
            x4 |= (text1[i1++] & 0xff) << 8;
            
            do {
                // 1) Multiply (modulo 0x10001), 1st text sub-block
                // with 1st key sub-block.
                x1 = (int) ((long) x1 * key[ik++] % 0x10001L & 0xffff);
                
                // 2) Add (modulo 0x10000), 2nd text sub-block
                // with 2nd key sub-block.
                x2 = x2 + key[ik++] & 0xffff;
                
                // 3) Add (modulo 0x10000), 3rd text sub-block
                // with 3rd key sub-block.
                x3 = x3 + key[ik++] & 0xffff;
                
                // 4) Multiply (modulo 0x10001), 4th text sub-block
                // with 4th key sub-block.
                x4 = (int) ((long) x4 * key[ik++] % 0x10001L & 0xffff);
                
                // 5) XOR results from steps 1 and 3.
                t2 = x1 ^ x3;
                
                // 6) XOR results from steps 2 and 4.
                // Included in step 8.
                
                // 7) Multiply (modulo 0x10001), result of step 5
                // with 5th key sub-block.
                t2 = (int) ((long) t2 * key[ik++] % 0x10001L & 0xffff);
                
                // 8) Add (modulo 0x10000), results of steps 6 and 7.
                t1 = t2 + (x2 ^ x4) & 0xffff;
                
                // 9) Multiply (modulo 0x10001), result of step 8
                // with 6th key sub-block.
                t1 = (int) ((long) t1 * key[ik++] % 0x10001L & 0xffff);
                
                // 10) Add (modulo 0x10000), results of steps 7 and 9.
                t2 = t1 + t2 & 0xffff;
                
                // 11) XOR results from steps 1 and 9.
                x1 ^= t1;
                
                // 14) XOR results from steps 4 and 10. (Out of order).
                x4 ^= t2;
                
                // 13) XOR results from steps 2 and 10. (Out of order).
                t2 ^= x2;
                
                // 12) XOR results from steps 3 and 9. (Out of order).
                x2 = x3 ^ t1;
                
                x3 = t2; // Results of x2 and x3 now swapped.
                
            } while(--r != 0); // Repeats seven more rounds.
            
            // Final output transform (4 steps).
            
            // 1) Multiply (modulo 0x10001), 1st text-block
            // with 1st key sub-block.
            x1 = (int) ((long) x1 * key[ik++] % 0x10001L & 0xffff);
            
            // 2) Add (modulo 0x10000), 2nd text sub-block
            // with 2nd key sub-block. It says x3, but that is to undo swap
            // of subblocks 2 and 3 in 8th processing round.
            x3 = x3 + key[ik++] & 0xffff;
            
            // 3) Add (modulo 0x10000), 3rd text sub-block
            // with 3rd key sub-block. It says x2, but that is to undo swap
            // of subblocks 2 and 3 in 8th processing round.
            x2 = x2 + key[ik++] & 0xffff;
            
            // 4) Multiply (modulo 0x10001), 4th text-block
            // with 4th key sub-block.
            x4 = (int) ((long) x4 * key[ik++] % 0x10001L & 0xffff);
            
            // Repackage from 16-bit sub-blocks to 8-bit byte array text2.
            text2[i2++] = (byte) x1;
            text2[i2++] = (byte) (x1 >>> 8);
            text2[i2++] = (byte) x3; // x3 and x2 are switched
            text2[i2++] = (byte) (x3 >>> 8); // only in name.
            text2[i2++] = (byte) x2;
            text2[i2++] = (byte) (x2 >>> 8);
            text2[i2++] = (byte) x4;
            text2[i2++] = (byte) (x4 >>> 8);
        }
    }
}