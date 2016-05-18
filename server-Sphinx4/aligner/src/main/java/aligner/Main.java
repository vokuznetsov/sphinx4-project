package aligner;

import good.of.pronunciation.ExtractGOPs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author vkuzn on 10.12.2015.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

//    private static final String NATIVE_NON_NATIVE = "audio/16kHz_16bit_native/";
//    private static final String KEY_WORD = "development";
//    private static final String AUDIO_TRACK = "development_male_1.wav.wav";
//    private static final String PRONUNCIATION = "NATIVE";
    private static final String pathToFolder = "C:\\Users\\vkuzn\\Documents\\IdeaProjects\\CourseWork\\" +
        "sphinx4-project\\server-Sphinx4\\aligner\\src\\main\\resources\\audio\\16kHz_16bit_native\\centrifuge";

    public static void main(String[] args) {
        ExtractGOPs extractGOPs = new ExtractGOPs();

        try {
            extractGOPs.getGOPs(pathToFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
