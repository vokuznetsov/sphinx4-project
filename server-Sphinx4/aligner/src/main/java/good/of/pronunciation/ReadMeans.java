package good.of.pronunciation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vkuzn on 24.04.2016.
 */
public class ReadMeans {

    private static final Logger log = LoggerFactory.getLogger(ReadMeans.class);
    private Map<Integer, Map<Integer, ArrayList<Double>>> means;

    public ReadMeans() {
        means = new HashMap<>();
    }

    public Map<Integer, Map<Integer, ArrayList<Double>>> getMeans() throws IOException {
        String file = "/models/acoustic/16khz/means.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(openFile(file)));

        in.readLine();      // skip first string
        String str;
        String letter;
        int row = -1;
        List<Double> values = new ArrayList<>();
        while ((str = in.readLine()) != null) {
            if (str.contains("mgau")) {
                letter = str.substring(5, str.length());
            } else if (str.contains("feat")) {
                row = Integer.parseInt(str.substring(5, str.length()));
            } else {
                // replace 2 or more space with single space
                str = str.trim().replaceAll(" +", " ");
                str = str.substring(str.indexOf(" ", 8) + 1, str.length());
                List<Integer> spaces = findAllCharacterInString(str, " ");

                for (int i = 0; i < spaces.size(); i++) {
                    if (i == 0) {
                        values.add(Double.valueOf(str.substring(i, spaces.get(i) + 1)));
                        values.add(Double.valueOf(str.substring(spaces.get(i) + 1, spaces.get(i + 1))));
                    }
                    else if (i == spaces.size() - 1)
                        values.add(Double.valueOf(str.substring(spaces.get(i) + 1, str.length())));
                    else
                        values.add(Double.valueOf(str.substring(spaces.get(i) + 1, spaces.get(i + 1))));

                }
               // log.info("readMeans test");
            }

        }


        return means;
    }


    /**
     * @param path - path to means file
     * @return - input stream
     * @throws FileNotFoundException
     */
    private InputStream openFile(String path) throws FileNotFoundException {
        InputStream is = this.getClass().getResourceAsStream(path);
        return is;
    }

    /**
     * Find all positions @symbol in str
     *
     * @return - array of occurrences @symbol in @str
     */
    private List<Integer> findAllCharacterInString(String str, String symbol) {
        List<Integer> occurrence = new ArrayList<>();
        int index = str.indexOf(symbol);

        while (index >= 0) {
            occurrence.add(index);
            index = str.indexOf(symbol, index + 1);
        }
        return occurrence;
    }
}
