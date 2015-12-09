import java.io.IOException;


/**
 * @author vkuzn on 03.12.2015.
 */
public class Main {
    public static void main(String[] args){

        String source = "parser/src/main/resources/0467.dic.txt";
        String output = "parser/src/main/resources/0467.phonemes.dic";
        String words = "parser/src/main/resources/words.txt";
        Parser parser = new Parser(source,output, words);
        try {
            parser.parser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
