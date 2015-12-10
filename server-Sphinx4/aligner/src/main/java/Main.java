import java.io.IOException;

/**
 * @author vkuzn on 10.12.2015.
 */
public class Main {
    private static final String TEXT = "development";
    //private static final String TEXT = "one zero zero zero one nine oh two one oh zero one eight zero three";
    public static void main(String[] args){
        try {
            Aligner aligner = new Aligner(TEXT);
            aligner.aligner();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
