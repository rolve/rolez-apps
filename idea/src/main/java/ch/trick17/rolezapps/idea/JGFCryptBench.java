package ch.trick17.rolezapps.idea;

import jgfutil.JGFInstrumentor;

public class JGFCryptBench extends IDEATest {
    
    private final int datasizes[] = {3_000_000, 20_000_000, 50_000_000};
    
    public JGFCryptBench(int threads) {
        super(threads);
    }
    
    public void initialize(int size) {
        arrayRows = datasizes[size];
        buildTestData();
    }
    
    public void validate() {
        boolean error;
        
        error = false;
        for(int i = 0; i < arrayRows; i++) {
            error = (plain1[i] != plain2[i]);
            if(error) {
                System.out.println("Validation failed");
                System.out.println("Original Byte " + i + " = " + plain1[i]);
                System.out.println("Encrypted Byte " + i + " = " + crypt1[i]);
                System.out.println("Decrypted Byte " + i + " = " + plain2[i]);
                //break;
            }
        }
    }
    
    public void JGFrun(int size) {
        JGFInstrumentor.addTimer("Section2:Crypt:Kernel", "Kbyte", size);
        
        initialize(size);
        run();
        validate();
        
        JGFInstrumentor.addOpsToTimer("Section2:Crypt:Kernel", (2 * arrayRows) / 1000.);
        JGFInstrumentor.printTimer("Section2:Crypt:Kernel");
    }
}
