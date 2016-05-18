package good.of.pronunciation;

import aligner.Aligner;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author vkuzn on 18-May-16.
 */
public class ExtractGOPs {
    private static final Logger log = LoggerFactory.getLogger(ExtractGOPs.class);

    private String PATH_TO_FOLDER = "audio/16kHz_16bit_native/";
    private static final String PRONUNCIATION = "NATIVE";

    private ReadMDEF readMDEF;
    private ReadMeansVariances readMeans;
    private ReadMixtureWeights readMixtureWeights;
    private FullMFCCfeatures fullMFCCfeatures;

    private MDEF mdef;
    private Map<Integer, Map<Integer, List<Double>>> means;
    private Map<Integer, Map<Integer, List<Double>>> variances;
    private Map<Integer, List<Double>> mixw;


    public ExtractGOPs() {
        readMDEF = new ReadMDEF();
        readMeans = new ReadMeansVariances();
        readMixtureWeights = new ReadMixtureWeights();
        fullMFCCfeatures = new FullMFCCfeatures();

        try {
            mdef = readMDEF.readMDEF();
            means = readMeans.getMeans();
            variances = readMeans.getVariances();
            mixw = readMixtureWeights.parseFile();
        } catch (IOException e) {
            mdef = null;
            means = null;
            variances = null;
            mixw = null;
        }

    }

    public void getGOPs(String folder) throws IOException {


        Files.walk(Paths.get(folder)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                String keyWord = filePath.getParent().getFileName().toString();
                String audioTrack = filePath.getFileName().toString();

                getProbability(keyWord, audioTrack);
            }
        });
    }

    private void getProbability(String keyWord, String audioTrack) {

        try {
            //---------------------------- MFCC FEATURES ----------------------------
            GetFirstMFCCfeatures features = new GetFirstMFCCfeatures(PATH_TO_FOLDER, keyWord, audioTrack);
            Map<Integer, List<Double>> firstFrames = features.extactFeatures();
            Map<Integer, List<Double>> frames = fullMFCCfeatures.getFullMFCCfeatures(firstFrames);

            //---------------------------- GET WORD RESULT ----------------------------
            Aligner aligner = new Aligner(PATH_TO_FOLDER, keyWord, audioTrack);
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
                    + "Audio track   :     " + audioTrack.substring(0, audioTrack.length() - 4) + "\n"
                    + "Word          :     " + keyWord.toUpperCase() + "\n"
                    + "Probability   :     " + sum / probability.size() + "\n"
                    + "#######################################################\n");
        } catch (IOException e) {
            log.info(e.toString());
        }
    }
}
