package home.vkuzn;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.util.LogMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;


/**
 * @param configuration          - задаем акустическую и языковую модели и словарь.
 * @param keyWord                - ключевое слово, которое пытаемся распознать.
 * @param posteriorArrayList     - список, хранящий вероятность для каждого ключевого слова.
 *                               В ходе одного распознования может быть несколько ключевых слов.
 * @param keyWordArrayList       - список из ключевых слов и/или слов, которые сожержат в себе ключеове слово.
 * @param pronunciationArrayList - транскрипция для каждого слова из keyWordArrayList.
 */

public class Recognition {
    private static final Logger log = LoggerFactory.getLogger(Recognition.class);

    private String keyWord;
    private ArrayList<Double> posteriorArrayList;
    private ArrayList<String> keyWordArrayList;
    //private ArrayList<String> pronunciationArrayList;
    private LogMath logMath;
    private StreamSpeechRecognizer streamSpeechRecognizer;

    public Recognition() throws IOException {

        Configuration configuration = new Configuration();

        // configuration from my files
        configuration.setDictionaryPath("resource:/models/en-us/language model and dictionary/1555.dic");
        configuration.setLanguageModelPath("resource:/models/en-us/language model and dictionary/1555.lm");
        //configuration.setAcousticModelPath("resource:/models/en-us/acoustic/8khz-5.1");

        // configuration from dependency *sphinx4-data*
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        //configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        //configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

        //pronunciationArrayList = new ArrayList<String>();
        posteriorArrayList = new ArrayList<Double>();
        keyWordArrayList = new ArrayList<String>();
        logMath = LogMath.getLogMath();
        streamSpeechRecognizer = new StreamSpeechRecognizer(configuration);
    }

    public void fileRecognize(String _keyWord) {
        try {
            keyWord = _keyWord;
            posteriorArrayList = new ArrayList<Double>();
            keyWordArrayList = new ArrayList<String>();

            streamSpeechRecognizer.startRecognition(new FileInputStream("src/test/native_pronunciation/extra/extra_female_3.wav"));
            SpeechResult result;

            while ((result = streamSpeechRecognizer.getResult()) != null) {
                getHypothesis(result);
                if (result.getHypothesis().equalsIgnoreCase("")) {
                    log.info("Please, repeat word" + "\n");
                    System.out.println("Please, repeat word");
                } else {
                    if (result.getWords().size() > 0) {
                        getPosterior(result);
                        getConfidence(result);
                    }
                    log.info(result.getHypothesis() + "\n");
                    System.out.println(resultOfSpeechRecognition());
                }
            }
            streamSpeechRecognizer.stopRecognition();
        } catch (Exception e)                 // тут ловим IllegalStateException или DataProcessingException исключения, для удобства объедили их в одно исключение
        {                                   // так как действия при ловле данных исключений одни и те же.
            System.out.println("ERROR - 1!!!");
            log.info("ERROR - 1!!!");
            //log.info(e.getMessage());
        }
    }

    public void speechAligner() {
        
    }

    /**
     * В качестве параметра принимаем входной поток (DataInputStream) и распознаем его.
     * Производим запись в файл и вывод на консоль наиболее важной информации
     * ( функции getHypothesis(), getPosterior(), getConfidence() ).
     */
    public String recognize(DataInputStream dataInputStream, String _keyWord) {
        try {
            keyWord = _keyWord.toLowerCase();
            posteriorArrayList = new ArrayList<Double>();
            keyWordArrayList = new ArrayList<String>();
            //pronunciationArrayList = new ArrayList<String>();

            streamSpeechRecognizer.startRecognition(dataInputStream);
            SpeechResult result = streamSpeechRecognizer.getResult();

            if (result != null) {
                getHypothesis(result);
                if (result.getHypothesis().equalsIgnoreCase("")) {
                    log.info("Please, repeat word" + "\n");

                    streamSpeechRecognizer.stopRecognition();
                    return "Please, repeat word";
                } else {
                    if (result.getWords().size() > 0) {
                        getPosterior(result);
                        getConfidence(result);
                    }

                    log.info("Hypothesis: " + result.getHypothesis() + "\n");
                    streamSpeechRecognizer.stopRecognition();
                    log.info("Result of speech recognition: " + resultOfSpeechRecognition() + "\n");
                    return resultOfSpeechRecognition();
                }
            } else {
                log.info("I don't hear you!" + "\n");
                streamSpeechRecognizer.stopRecognition();
                return "I don't hear you!";
            }
        } catch (Exception e)                 // тут ловим IllegalStateException или DataProcessingException исключения, для удобства объедили их в одно исключение
        {                                   // так как действия при ловле данных исключений одни и те же.
            System.out.println("ERROR - 1!!!");
            log.info("ERROR - 1!!!");
            try {
                if (dataInputStream != null)
                    dataInputStream.close();
            } catch (IOException e1) {
                log.info(e1.getMessage());
                e1.printStackTrace();
            }
            log.info("Error data reading!");
            return "Error data reading!";
        }
    }

    /**
     * Печатаем 3 наиболее вероятные гипотезы в файл и выводим на консоль.
     */
    private void getHypothesis(SpeechResult result) {
        log.info("**************************");
        for (String s : result.getNbest(3)) {
            System.out.println("Hypothesis: " + s);
            log.info("Hypothesis: " + s);
        }
    }

    /**
     * Среди всех возможных слов, которые распознал Sphinx-4, выводим те, у которых Posterior(ая) вероятность больше 0.001
     * и те, которые содержат keyWord(ключевое слово), которое пытается сказать пользователь.
     *
     * @param posteriorArrayList - содержит вероятности всех ключевых слов (Word-KEY или keyWord),
     *                           а так же слов, в которое входит ключевое слово.
     *                           keyWordArrayList -   сожержит node(ы), которые идентичны ключевому слову
     *                           или содержат ключевое слово.
     */
    private void getPosterior(SpeechResult result) {
        Collection<Node> nodes = result.getLattice().getNodes();
        log.info("----------------POSTERIOR----------------");
        for (Node node : nodes) {
            if (logMath.logToLinear((float) node.getPosterior()) > 0.001) {
                log.info(" Word > 0.001: " + node.getWord().toString() + " LIN: " + logMath.logToLinear((float) node.getPosterior()));
            }

            if (node.getWord().toString().toLowerCase().contains(getKeyWord(keyWord))) {
                double posterior = logMath.logToLinear((float) node.getPosterior());
                log.info(" Word-KEY: " + node.getWord().toString() + " LIN: " + posterior + " Pronunciation:" + node.getWord().getMostLikelyPronunciation().toDetailedString());

                posteriorArrayList.add(posterior);  // хранит вероятности ключевых слов.
                keyWordArrayList.add(node.getWord().toString());    // хранит ключевые слова, а так же слова в которых встречается keyWord.
                //pronunciationArrayList.add(node.getWord().getMostLikelyPronunciation().toString());     // Показывает транскрипцию слова.
            }
        }
    }

    /**
     * Выводим на консоль и записываем в файл слово, наиболее вероятное по Sphinx-4, и его вероятность (Confidence).
     */
    private void getConfidence(SpeechResult result) {
        log.info("----------------CONFIDENCE----------------");
        for (int i = 0; i < result.getWords().size(); i++) {
            System.out.println("Confidence: " + logMath.logToLinear((float) result.getWords().get(i).getConfidence())
                    + " Word: " + result.getWords().get(i).getPronunciation().getWord());  // Выводим слово, которое распознал нам Sphinx-4 и его вероятность.

            log.info("Confidence: " + logMath.logToLinear((float) result.getWords().get(i).getConfidence())
                    + " Word: " + result.getWords().get(i).getPronunciation().getWord());
        }
    }

    /**
     * @param maxPosterior        - возвращает максимальную вероятность ключевого слова или его вхождения, в другое слово.
     *                            В качестве начального значения (вместо "-1") можно задать любое отрицательное число,
     *                            т.к. вероятность всегда будет больше, либо равна 0.
     * @param indexOfMaxPosterior - индекс слова с максимальной вероятностью.
     *                            Необходим, чтобы потом правильно извлечь выбранное ключевое слово.
     * @return - возвращает индекс ключевого слова (Word-KEY или keyWord) с максимальной вероятностью
     * или того слова, в котором ключевое слово сожержится.
     * Возвращает -1, если posteriorArrayList - пустой или, другими словами,
     * при распозновании Sphinx-4 не нашал ни одного ключевого слова.
     */
    public int getMaxPosteriorOfWordKEY() {
        int indexOfMaxPosterior = -1;
        double maxPosterior = -1;
        if (!posteriorArrayList.isEmpty()) {
            for (int i = 0; i < posteriorArrayList.size(); i++) {
                if (maxPosterior < posteriorArrayList.get(i)) {
                    maxPosterior = posteriorArrayList.get(i);
                    indexOfMaxPosterior = i;
                }
            }
        }
        return indexOfMaxPosterior;
    }

    /**
     * @return возвращает строку, содержащую слово, и вероятность, с которой это слово произнесли.
     * Если вероятность произнесенного слова меньше, чем 0.01%, то выдаем выдаем вероятность 0%.
     */
    public String resultOfSpeechRecognition() {
        int indexOfRecognitionWord = getMaxPosteriorOfWordKEY();
        if (indexOfRecognitionWord != -1 && posteriorArrayList.get(indexOfRecognitionWord) > 0.0001) {
            String word = keyWordArrayList.get(indexOfRecognitionWord);
            double probability = posteriorArrayList.get(indexOfRecognitionWord) * 100;
            if (probability > 100)
                probability = 100;
            String result = String.format(" %.3f", probability);

            //return word + " " + result + "%" + " pronunciation: " + pronunciationArrayList.get(indexOfRecognitionWord);
            return word.toLowerCase() + " " + result.toLowerCase() + "%";
        } else
            return keyWord + " 0.00%";
    }

    /**
     * Данная функция возвращает под строку от ключевого слова, если ее длина больше, либо равна 7.
     * Эта функция необходима для нахождения большего количества вариаций от ключевого слова.
     * Например, слово "recognise". Если мы оставим слово как есть, то в списке распознаных слов будут только те слова,
     * которые содержат слово "recognise", а если мы убирем две последние буквы ("recogni"),
     * то в списке распознаных слов могут быть такие слова как "recognition" и др. вариации.
     * Т. к. система распознования (Sphinx-4) не совершенна и зачастую выдает различные вариации от сказанного слова,
     * поэтому данная функция будет уместна.
     */
    public String getKeyWord(String _keyWord) {
        if (_keyWord != null && _keyWord.length() >= 7)
            return _keyWord.substring(0, _keyWord.length() - 2);
        else
            return _keyWord;
    }
}