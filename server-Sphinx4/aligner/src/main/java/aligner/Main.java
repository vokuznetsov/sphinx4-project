package aligner;

import good.of.pronunciation.FullMFCCfeatures;
import good.of.pronunciation.GetFirstMFCCfeatures;
import good.of.pronunciation.MDEF;
import good.of.pronunciation.ReadMDEF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author vkuzn on 10.12.2015.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static String keyWord = "development";
    //private static final String TEXT = "one zero zero zero one nine oh two one oh zero one eight zero three";

    //aligner.Features
    private static String audioTrack = "development_male_1.wav.wav";
    public static void main(String[] args){
        try {
            Aligner aligner = new Aligner(keyWord, audioTrack);
            aligner.aligner();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // ---------------------------- MFCC FEATURES ----------------------------
//        GetFirstMFCCfeatures features = new GetFirstMFCCfeatures(audioTrack, keyWord);
//        features.extactFeatures();
//
//        Map<Integer, List<Float>> firstFrames = features.readMFCCfile();
//
//        FullMFCCfeatures fullMFCCfeatures = new FullMFCCfeatures(firstFrames);
//        fullMFCCfeatures.getFullMFCCfeatures();

        // ---------------------------- READ MDEF ----------------------------

        ReadMDEF readMDEF = new ReadMDEF();
        try {
            MDEF mdef = readMDEF.readMDEF();
            log.info("Test mdef");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
