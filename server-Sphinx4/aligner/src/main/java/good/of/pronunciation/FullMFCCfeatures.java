package good.of.pronunciation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author vkuzn on 17.04.2016.
 *         <p>
 *         Compute last 26 values of mfcc feature.
 */
public class FullMFCCfeatures {

    private static final Logger log = LoggerFactory.getLogger(FullMFCCfeatures.class);

    private static int FIRST_FRAMES = 13;
    private static int SECOND_FRAMES = 26;


    // 13 firsts mfcc features. Get from GetFirstMFCCfeatures.class
    private Map<Integer, List<Float>> firstFrames;
    private Map<Integer, List<Float>> fullFrames;

    public FullMFCCfeatures(Map<Integer, List<Float>> firstFrames) {
        this.firstFrames = new HashMap<>(firstFrames);
        fullFrames = new HashMap<>(firstFrames);
    }


    /**
     * first frames  -  0-12
     * second frames - 13-25
     * third frames  - 27-38
     */
    public Map<Integer, List<Float>> getFullMFCCfeatures() {

        Map<Integer, List<Float>> secondFrames = new HashMap<>();

        // (-3) because we exclude 2 last frames. We can't compute second and third frames for their.
        for (int i = 0; i < firstFrames.size() - 1; i++) {

            for (int j = 0; j < FIRST_FRAMES; j++) {
                fullFrames.get(i).add(firstFrames.get(i).get(j) - firstFrames.get(i + 1).get(j));
            }
        }

        for (int i = 0; i < firstFrames.size() - 2; i++) {
            for (int j = FIRST_FRAMES; j < SECOND_FRAMES; j++) {
                fullFrames.get(i).add(fullFrames.get(i).get(j) - fullFrames.get(i + 1).get(j));
            }
        }

        return fullFrames;
    }
}
