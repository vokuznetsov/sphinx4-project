import edu.cmu.sphinx.api.SpeechAligner;
import edu.cmu.sphinx.result.WordResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * It is a alpha version of speech aligner. There are a lot of bugs.
 *
 * @author vkuzn on 10.12.2015.
 */
public class Aligner {
    private static final Logger log = LoggerFactory.getLogger(Aligner.class);

    private static final String acousticModel = "resource:/edu/cmu/sphinx/models/en-us/en-us";
    //private static final String acousticModel = "resource:/models/en-us/acoustic/8khz-5.1";
    //private static final String dictionaryPath = "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    private static final String dictionaryPath = "resource:/models/en-us/language model and dictionary/phonemes.dic";

    private String keyWords;
    private SpeechAligner speechAligner;
    private URL audio;


    public Aligner(String keyWords) throws IOException {

        this.keyWords = keyWords;
        speechAligner = new SpeechAligner(acousticModel, dictionaryPath, null);
        audio = this.getClass().getClassLoader().getResource("development_female_9.wav");
    }

    public void aligner() throws IOException {
        List<String> listOfTranscriptions = getTranscriptions();
        if (!listOfTranscriptions.isEmpty()) {
            //List<WordResult> results  = speechAligner.align(audio,listOfTranscriptions.get(0));         // 0  is temporary,  further I remake the logic of it
            List<WordResult> results  = speechAligner.align(audio,keyWords);
            List<String> stringResults = new ArrayList<String>();
            for (WordResult wr : results) {
                stringResults.add(wr.getWord().getSpelling());
                System.out.println(wr.toString());
            }
        } else {
            log.info("This word is not contained in dictionary");
        }

    }

    /**
     * This method extracts phonemes(transcriptions) from dictionary for the choosen word.
     * @return - list of transcriptions for the word
     * @throws IOException
     */
    private List<String> getTranscriptions() throws IOException {
        String dictionary = "/models/en-us/language model and dictionary/dictionary.dic";
        List<String> listOfTranscriptions = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new InputStreamReader(openFile(dictionary)));

        String str;
        String transcription;
        while ((str = in.readLine()) != null){
            if (str.toLowerCase().contains(keyWords)){
                transcription = str.substring(str.indexOf(" ") + 1, str.length());
                listOfTranscriptions.add(transcription);
            }
        }

        return listOfTranscriptions;
    }


    /**
     * @param path - path to dictionary
     * @return - input stream
     * @throws FileNotFoundException
     */
    private InputStream openFile(String path) throws FileNotFoundException {
        InputStream is = this.getClass().getResourceAsStream(path);
        return is;
    }
}
