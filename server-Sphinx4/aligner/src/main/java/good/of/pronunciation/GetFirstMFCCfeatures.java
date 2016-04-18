package good.of.pronunciation;

import edu.cmu.sphinx.tools.feature.FeatureFileDumper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vkuzn on 17.04.2016.
 *
 *
 * Compute first 13 (from 39) values of mfcc feature.
 */
public class GetFirstMFCCfeatures {

    private static final Logger log = LoggerFactory.getLogger(GetFirstMFCCfeatures.class);

    private String audioTrack;
    private String keyWord;
    private String inputAudioTrack;
    private String outputFile;

    public GetFirstMFCCfeatures(String audioTrack, String keyWord) {
        this.audioTrack = audioTrack;
        this.keyWord = keyWord;
        try {
            this.inputAudioTrack = GetFirstMFCCfeatures.class.getClassLoader().getResource("audio/16kHz_16bit_native/"
                    + keyWord + "/" + audioTrack).getPath();
        } catch (NullPointerException e) {
            this.inputAudioTrack = "";
        }

        outputFile = "C:\\Users\\Vladimir\\Documents\\IdeaProjects\\Git\\sphinx4-project\\" +
                "server-Sphinx4\\extraction\\features\\" + "features_"
                + audioTrack.substring(0, audioTrack.length() - 7) + "txt";

//        outputFile = "C:\\Users\\vkuzn\\IdeaProjects\\CourseWork\\sphinx4-project\\server-Sphinx4\\extraction\\features\\"
//                + "features_"  + audioTrack.substring(0, audioTrack.length() - 7) + "txt";
    }

    public void extactFeatures() {
        // -name cepstraFrontEnd, spectraFrontEnd, plpFrontEnd
        // -format binary/ascii

        String[] args1 = {"-name", "cepstraFrontEnd", "-i", inputAudioTrack, "-o", outputFile, "-format", "ascii"};
        FeatureFileDumper.main(args1);
    }

    public String getAudioTrack() {
        return audioTrack;
    }

    public void setAudioTrack(String audioTrack) {
        this.audioTrack = audioTrack;
    }

    public String getInputAudioTrack() {
        return inputAudioTrack;
    }

    public void setInputAudioTrack(String inputAudioTrack) {
        this.inputAudioTrack = inputAudioTrack;
    }

    public Map<Integer, List<Float>> readMFCCfile() {

        try (BufferedReader br = new BufferedReader(new FileReader(outputFile))) {

            String sCurrentLine;
            Map<Integer, List<Float>> frames = new HashMap<>();

            if ((sCurrentLine = br.readLine()) != null) {
                String[] values = sCurrentLine.substring(sCurrentLine.indexOf(" ") + 1, sCurrentLine.length()).split(" ");

                List<Float> var = new ArrayList<>();
                int count = 0;

                for (int i = 0; i < values.length; i++) {
                    if ((i + 1) % 13 == 0) {
                        var.add(Float.valueOf(values[i]));
                        frames.put(count, new ArrayList<>(var));
                        count++;
                        var.clear();
                    } else {
                        var.add(Float.valueOf(values[i]));
                    }
                }

                return frames;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
