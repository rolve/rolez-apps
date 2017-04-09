package ch.trick17.rolezapps.histogram;

import rolez.lang.GuardedArray;

public class HistogramRolezLocalOpt extends HistogramRolez {
    
    public HistogramRolezLocalOpt(GuardedArray<GuardedArray<int[]>[]> image, long $task) {
        super(image, $task);
    }
    
    @Override
    public void merge(HistPart histPart, long $task) {
        guardReadOnly(this, $task);
        guardReadWrite(this.rHist);
        guardReadWrite(this.gHist);
        guardReadWrite(this.bHist);
        guardReadOnly(histPart.r, $task);
        guardReadOnly(histPart.g, $task);
        guardReadOnly(histPart.b, $task);
        for(int c = 0; c < 256; c++) {
            rHist.data[c] += histPart.r.data[c];
            gHist.data[c] += histPart.g.data[c];
            bHist.data[c] += histPart.b.data[c];
        }
    }
}
