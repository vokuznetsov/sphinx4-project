import edu.cmu.sphinx.tools.feature.FeatureFileDumper;

/**
 * @author vkuzn on 14.02.2016.
 */
public class Features {
    //private String config = "C:\\Users\\Vladimir\\Documents\\IdeaProjects\\Git\\sphinx4-project\\server-Sphinx4\\aligner\\src\\main\\resources\\config.txt";
    private String audioTrack;
    private String inputAudioTrack;
    private String outputFile;

    public Features(String audioTrack, String inputAudioTrack){
        this.audioTrack = audioTrack;
        this.inputAudioTrack = inputAudioTrack;
        outputFile = "C:\\Users\\Vladimir\\Documents\\IdeaProjects\\Git\\sphinx4-project\\" +
                "server-Sphinx4\\extraction\\features\\" + "features_"
                + audioTrack.substring(0,audioTrack.length()-7)+"txt";
    }

    public void extactFeatures(){
        // -name cepstraFrontEnd, spectraFrontEnd, plpFrontEnd
        // -format binary/ascii

        String[] args1 = {"-name","cepstraFrontEnd", "-i", inputAudioTrack, "-o", outputFile, "-format", "ascii"};
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
}
