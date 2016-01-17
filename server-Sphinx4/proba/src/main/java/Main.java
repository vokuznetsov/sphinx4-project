import java.io.IOException;

/**
 * @author vkuzn on 10.12.2015.
 */
public class Main {
    //private static final String TEXT = "development";
    private static final String TEXT = "d e v e l o p m e n t";//zero zero one nine oh two one oh zero one eight zero three
    public static void main(String[] args){
        try {
            Aligner aligner = new Aligner(TEXT);
            aligner.recognize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
