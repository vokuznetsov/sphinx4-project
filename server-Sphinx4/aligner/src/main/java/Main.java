import java.io.IOException;

/**
 * @author vkuzn on 10.12.2015.
 */
public class Main {
    //Aligner
    private static String keyWord = "colored";
    //private static final String TEXT = "one zero zero zero one nine oh two one oh zero one eight zero three";

    //Features
    private static String audioTrack = "colored_female_3.wav.wav";
    private static String inputAudioTrack = Features.class.getClassLoader().getResource("test/16kHz_16bit_native/"
            + keyWord + "/" + audioTrack).getPath();
    public static void main(String[] args){
        try {
            Aligner aligner = new Aligner(keyWord, audioTrack);
            aligner.aligner();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Features features = new Features(audioTrack,inputAudioTrack);
        features.extactFeatures();

    }
}
