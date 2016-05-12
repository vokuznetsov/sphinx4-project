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

    //private static String keyWord = "development";
    private static String keyWord = "pizzeria";
    //private static final String TEXT = "one zero zero zero one nine oh two one oh zero one eight zero three";

    private static Map<TimeFrame, Map<Integer, List<Double>>> mfccFeaturesForPhonemes;

    //private static String audioTrack = "development_male_1.wav.wav";
    private static String audioTrack = "pizzeria_female_2.wav.wav";

    public static void main(String[] args) {
        List<WordResult> wordResults = new ArrayList<>();
        mfccFeaturesForPhonemes = new HashMap<>();
        try {
            Aligner aligner = new Aligner(keyWord, audioTrack);
            wordResults = aligner.aligner();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //---------------------------- MFCC FEATURES ----------------------------
        GetFirstMFCCfeatures features = new GetFirstMFCCfeatures(audioTrack, keyWord);
        features.extactFeatures();

        Map<Integer, List<Double>> firstFrames = features.readMFCCfile();

        FullMFCCfeatures fullMFCCfeatures = new FullMFCCfeatures(firstFrames);
        Map<Integer, List<Double>> frames = fullMFCCfeatures.getFullMFCCfeatures();
        log.info("frames");

        // ---------------------------- ACOUSTIC SEGMENT (O(p)) ----------------------------
        AcousticSegment acousticSegment = new AcousticSegment(frames);
        mfccFeaturesForPhonemes = wordResults.stream().collect(Collectors.toMap
                (WordResult::getTimeFrame, acousticSegment::getAcousticSegment));


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

            GOP gop = new GOP(wordResults, mfccFeaturesForPhonemes, mdef, means, variances, mixw);
            List<Double> numerator = gop.numerator();
            List<Double> denominator = gop.denominator();

            List<Double> probability = new ArrayList<>();
            for (int i = 0; i < denominator.size(); i++) {
                double result = Math.log(numerator.get(i) / denominator.get(i));
                probability.add(Math.abs(result));
            }

            log.info("numerator: " + numerator);
            log.info("denominator: " + denominator);

            log.info("Probability: " + probability.toString());

            double sum = probability.stream().reduce((aDouble, aDouble2) -> aDouble + aDouble2).orElse(0.0);
            log.info("Sum of probabilities: " + sum / probability.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
