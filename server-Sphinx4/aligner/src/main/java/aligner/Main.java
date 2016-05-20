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


    private static final String pathToFolder = "C:\\Users\\Vladimir\\Documents\\IdeaProjects\\Git\\" +
        "sphinx4-project\\server-Sphinx4\\aligner\\src\\main\\resources\\audio\\16kHz_16bit_native";

    public static void main(String[] args) {
        ExtractGOPs extractGOPs = new ExtractGOPs();

        try {
            extractGOPs.getGOPs(pathToFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
