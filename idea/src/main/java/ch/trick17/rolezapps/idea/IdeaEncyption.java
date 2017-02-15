package ch.trick17.rolezapps.idea;

import java.util.Arrays;
import java.util.Random;

/**
 * This test performs IDEA encryption then decryption. IDEA stands for International Data Encryption
 * Algorithm. The test is based on code presented in Applied Cryptography by Bruce Schnier, which
 * was based on code developed by Xuejia Lai and James L. Massey.
 */
public class IdeaEncyption {
    
    private final int size;
    private final int threads;
    
    private byte[] plain;
    private byte[] encrypted;
    private byte[] decrypted;
    
    private short[] userKey;
    private int[] encryptKey; // userkey derived
    private int[] decryptKey; // userkey derived
    
    
    public IdeaEncyption(int size, int threads) {
        this.size = size;
        this.threads = threads;
    }
    
    /**
     * Builds the data used for the test
     */
    public void buildTestData() {
        plain = new byte[size];
        encrypted = new byte[size];
        decrypted = new byte[size];
        
        Random random = new Random(136506717L); // Create random number generator.
        
        // Allocate three arrays to hold keys: userkey is the 128-bit key.
        // encryptKey is the set of 16-bit encryption subkeys derived from userkey,
        // while decryptKey is the set of 16-bit decryption subkeys also derived
        // from userkey. NOTE: The 16-bit values are stored here in
        // 32-bit int arrays so that the values may be used in calculations
        // as if they are unsigned. Each 64-bit block of plaintext goes
        // through eight processing rounds involving six of the subkeys
        // then a final output transform with four of the keys; (8 * 6)
        // + 4 = 52 subkeys.
        
        userKey = new short[8]; // User key has 8 16-bit shorts.
        
        // Generate user key randomly; eight 16-bit values in an array.
        for(int i = 0; i < 8; i++) {
            // Again, the random number function returns int. Converting
            // to a short type preserves the bit pattern in the lower 16
            // bits of the int and discards the rest.
            userKey[i] = (short) random.nextInt();
        }
        
        // Compute encryption and decryption subkeys.
        encryptKey = calcEncryptKey(userKey);
        decryptKey = calcDecryptKey(encryptKey);
        
        // Fill plain with "text."
        for(int i = 0; i < size; i++) {
            plain[i] = (byte) i;
            // Converting to a byte type preserves the bit pattern in the lower 8 bits of the
            // int and discards the rest.
        }
    }
    
    /**
     * Builds the 52 16-bit encryption subkeys from the user key and stores in 32-bit int array. The
     * routing corrects an error in the source code in the Schnier book. Basically, the sense of the
     * 7- and 9-bit shifts are reversed. It still works reversed, but would encrypted code would not
     * decrypt with someone else's IDEA code.
     */
    private static int[] calcEncryptKey(short[] userKey) {
        int[] key = new int[52];
        for(int i = 0; i < 52; i++)
            key[i] = 0;
        
        for(int i = 0; i < 8; i++) // First 8 subkeys are userkey itself.
            key[i] = userKey[i] & 0xffff; // Convert "unsigned"
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
                key[i] = ((key[i - 7] >>> 9) | (key[i - 6] << 7)) & 0xFFFF; // Shift and combine. Just 16 bits.
                continue; // Next iteration.
            }
            
            if(j == 6) { // Wrap to beginning for second chunk.
                key[i] = ((key[i - 7] >>> 9) | (key[i - 14] << 7)) & 0xFFFF;
                continue;
            }
            
            // j == 7 so wrap to beginning for both chunks.
            key[i] = ((key[i - 15] >>> 9) | (key[i - 14] << 7)) & 0xFFFF;
        }
        return key;
    }
    
    /**
     * Builds the 52 16-bit encryption subkeys from the encryption subkeys.
     */
    private static int[] calcDecryptKey(int[] encryptKey) {
        int[] decryptKey = new int[52];
        int j, k; // Index counters.
        int t1, t2, t3; // Temps to hold decrypt subkeys.
        
        t1 = inv(encryptKey[0]); // Multiplicative inverse (mod x10001).
        t2 = -encryptKey[1] & 0xffff; // Additive inverse, 2nd encrypt subkey.
        t3 = -encryptKey[2] & 0xffff; // Additive inverse, 3rd encrypt subkey.
        
        decryptKey[51] = inv(encryptKey[3]); // Multiplicative inverse (mod x10001).
        decryptKey[50] = t3;
        decryptKey[49] = t2;
        decryptKey[48] = t1;
        
        j = 47; // Indices into temp and encrypt arrays.
        k = 4;
        for(int i = 0; i < 7; i++) {
            t1 = encryptKey[k++];
            decryptKey[j--] = encryptKey[k++];
            decryptKey[j--] = t1;
            t1 = inv(encryptKey[k++]);
            t2 = -encryptKey[k++] & 0xffff;
            t3 = -encryptKey[k++] & 0xffff;
            decryptKey[j--] = inv(encryptKey[k++]);
            decryptKey[j--] = t2;
            decryptKey[j--] = t3;
            decryptKey[j--] = t1;
        }
        
        t1 = encryptKey[k++];
        decryptKey[j--] = encryptKey[k++];
        decryptKey[j--] = t1;
        t1 = inv(encryptKey[k++]);
        t2 = -encryptKey[k++] & 0xffff;
        t3 = -encryptKey[k++] & 0xffff;
        decryptKey[j--] = inv(encryptKey[k++]);
        decryptKey[j--] = t3;
        decryptKey[j--] = t2;
        decryptKey[j--] = t1;
        
        return decryptKey;
    }
    
    public void run() {
        Runnable thobjects[] = new Runnable[threads];
        Thread th[] = new Thread[threads];
        
        // Encrypt
        for(int i = 1; i < threads; i++) {
            thobjects[i] = new IDEARunner(i, plain, encrypted, encryptKey, threads);
            th[i] = new Thread(thobjects[i]);
            th[i].start();
        }
        
        thobjects[0] = new IDEARunner(0, plain, encrypted, encryptKey, threads);
        thobjects[0].run();
        
        for(int i = 1; i < threads; i++) {
            try {
                th[i].join();
            } catch(InterruptedException e) {}
        }
        
        // Decrypt
        for(int i = 1; i < threads; i++) {
            thobjects[i] = new IDEARunner(i, encrypted, decrypted, decryptKey, threads);
            th[i] = new Thread(thobjects[i]);
            th[i].start();
        }
        
        thobjects[0] = new IDEARunner(0, encrypted, decrypted, decryptKey, threads);
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
        if(!Arrays.equals(plain, decrypted))
            throw new AssertionError("Validation failed");
    }
}

class IDEARunner implements Runnable {
    
    private final int id;
    private final byte[] src, dest;
    private final int[] key;
    private final int threads;
    
    public IDEARunner(int id, byte[] src, byte[] dest, int[] key, int threads) {
        this.id = id;
        this.src = src;
        this.dest = dest;
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
        
        tslice = src.length / 8;
        ttslice = (tslice + threads - 1) / threads;
        slice = ttslice * 8;
        
        ilow = id * slice;
        iupper = (id + 1) * slice;
        if(iupper > src.length)
            iupper = src.length;
        
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
            
            x1 = src[i1++] & 0xff; // Build 16-bit x1 from 2 bytes,
            x1 |= (src[i1++] & 0xff) << 8; // assuming low-order byte first.
            x2 = src[i1++] & 0xff;
            x2 |= (src[i1++] & 0xff) << 8;
            x3 = src[i1++] & 0xff;
            x3 |= (src[i1++] & 0xff) << 8;
            x4 = src[i1++] & 0xff;
            x4 |= (src[i1++] & 0xff) << 8;
            
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
            dest[i2++] = (byte) x1;
            dest[i2++] = (byte) (x1 >>> 8);
            dest[i2++] = (byte) x3; // x3 and x2 are switched
            dest[i2++] = (byte) (x3 >>> 8); // only in name.
            dest[i2++] = (byte) x2;
            dest[i2++] = (byte) (x2 >>> 8);
            dest[i2++] = (byte) x4;
            dest[i2++] = (byte) (x4 >>> 8);
        }
    }
}
