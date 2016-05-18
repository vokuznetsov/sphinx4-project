package aligner;

import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import good.of.pronunciation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author vkuzn on 10.12.2015.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String NATIVE_NON_NATIVE = "audio/16kHz_16bit_native/";
    private static final String KEY_WORD = "development";
    private static final String AUDIO_TRACK = "development_male_1.wav.wav";
    private static final String PRONUNCIATION = "NATIVE";

    private static FullMFCCfeatures fullMFCCfeatures = new FullMFCCfeatures();

    public static void main(String[] args) {

        //---------------------------- READ MDEF ----------------------------
        ReadMDEF readMDEF = new ReadMDEF();

        //---------------------------- READ MEANS ----------------------------
        ReadMeansVariances readMeans = new ReadMeansVariances();

        //---------------------------- READ MIXTURE WEIGHTS ----------------------------
        ReadMixtureWeights readMixtureWeights = new ReadMixtureWeights();


        try {
            MDEF mdef = readMDEF.readMDEF();
            Map<Integer, Map<Integer, List<Double>>> means = readMeans.getMeans();
            Map<Integer, Map<Integer, List<Double>>> variances = readMeans.getVariances();
            Map<Integer, List<Double>> mixw = readMixtureWeights.parseFile();


            //---------------------------- MFCC FEATURES ----------------------------
            GetFirstMFCCfeatures features = new GetFirstMFCCfeatures(NATIVE_NON_NATIVE, AUDIO_TRACK, KEY_WORD);
            Map<Integer, List<Double>> firstFrames = features.extactFeatures();
            Map<Integer, List<Double>> frames = fullMFCCfeatures.getFullMFCCfeatures(firstFrames);

            //---------------------------- GET WORD RESULT ----------------------------
            Aligner aligner = new Aligner(NATIVE_NON_NATIVE, KEY_WORD, AUDIO_TRACK);
            List<WordResult> wordResults = aligner.aligner();

            // ---------------------------- ACOUSTIC SEGMENT (O(p)) ----------------------------
            AcousticSegment acousticSegment = new AcousticSegment(frames);
            Map<TimeFrame, Map<Integer, List<Double>>> mfccFeaturesForPhonemes = wordResults.stream().
                    collect(Collectors.toMap(WordResult::getTimeFrame, acousticSegment::getAcousticSegment));

            GOP gop = new GOP(wordResults, mfccFeaturesForPhonemes, mdef, means, variances, mixw);
            List<Double> numerator = gop.numerator();
            List<Double> denominator = gop.denominator();

            List<Double> probability = new ArrayList<>();
            for (int i = 0; i < denominator.size(); i++) {
                double result = Math.log(numerator.get(i) / denominator.get(i));
                probability.add(Math.abs(result));
            }


            double sum = probability.stream().reduce((aDouble, aDouble2) -> aDouble + aDouble2).orElse(0.0);
            log.info("\nPronunciation :     " + PRONUNCIATION + "\n"
                    + "Audio track   :     " + AUDIO_TRACK.substring(0, AUDIO_TRACK.length() - 4) + "\n"
                    + "Word          :     " + KEY_WORD.toUpperCase() + "\n"
                    + "Probability   :     " + sum / probability.size() + "\n"
                    + "#######################################################\n" );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
