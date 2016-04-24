package good.of.pronunciation;

import edu.cmu.sphinx.result.WordResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class returned acoustic segmentd of phoneme ( O(q) ).
 *
 * @author vkuzn on 24.04.2016.
 * @return - matrix with size 39x*number of frames*.
 * For example, if phoneme `d` is pronounced within 300-380 ms it means AcousticSegment return matrix 39x8 (380-300/10 = 8).
 */

public class AcousticSegment {

    private Map<Integer, List<Double>> frames;

    public AcousticSegment(Map<Integer, List<Double>> frames) {
        this.frames = frames;
    }

    public Map<Integer, List<Double>> getAcousticSegment(WordResult wordResult) {
        Map<Integer, List<Double>> acousticSegment = new HashMap<>();

        int start = (int) (wordResult.getTimeFrame().getStart() / 10);
        int end = (int) (wordResult.getTimeFrame().getEnd() / 10);

        for (int i = start; i <= end; i++) {
            List<Double> frame = frames.get(i);
            acousticSegment.put(i, frame);
        }
        return acousticSegment;
    }
}
