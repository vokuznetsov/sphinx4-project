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
public class ReadMeansVariances {

    private static final Logger log = LoggerFactory.getLogger(ReadMeansVariances.class);
    private final String means = "/models/acoustic/16khz/means.txt";
    private final String variances = "/models/acoustic/16khz/variances.txt";

    public ReadMeansVariances() {
    }

    public Map<Integer, Map<Integer, List<Double>>> getMeans() throws IOException {
        return parseFile(means);
    }

    public Map<Integer, Map<Integer, List<Double>>> getVariances() throws IOException {
        return parseFile(variances);
    }

    /**
     * Read file and return this file in Map view
     *
     * @param filename
     * @return - parse file
     * @throws IOException
     */
    private Map<Integer, Map<Integer, List<Double>>> parseFile(String filename) throws IOException {

        Map<Integer, Map<Integer, List<Double>>> result = new HashMap<>();

        BufferedReader in = new BufferedReader(new InputStreamReader(openFile(filename)));
        in.readLine();      // skip first string

        String str;
        int letter = 0;
        int count = 0;
        List<Double> values = new ArrayList<>();
        Map<Integer, List<Double>> matrix = new HashMap<>();

        while ((str = in.readLine()) != null) {
            if (str.contains("mgau")) {
                letter = Integer.parseInt(str.substring(5, str.length()));
                if (letter != 0) {
                    result.put(letter - 1, new HashMap<>(matrix));      // set previous phoneme to result
                    count = 0;
                    matrix.clear();
                }
            } else if (str.contains("feat")) {
            } else {
                values.clear();
                // replace 2 or more space with single space
                str = str.trim().replaceAll(" +", " ");

                int firstSpace = str.indexOf(" ");                          // first space is after density
                int secondSpace = str.indexOf(" ", firstSpace + 1);         // second space is after number of row
                str = str.substring(secondSpace + 1, str.length());
                List<Integer> spaces = findAllCharacterInString(str, " ");

                for (int i = 0; i < spaces.size(); i++) {
                    if (i == 0) {
                        values.add(Double.valueOf(str.substring(i, spaces.get(i) + 1)));
                        values.add(Double.valueOf(str.substring(spaces.get(i) + 1, spaces.get(i + 1))));
                    } else if (i == spaces.size() - 1)
                        values.add(Double.valueOf(str.substring(spaces.get(i) + 1, str.length())));
                    else
                        values.add(Double.valueOf(str.substring(spaces.get(i) + 1, spaces.get(i + 1))));

                }
                matrix.put(count, new ArrayList<>(values));
                count++;
            }
        }

        result.put(letter, new HashMap<>(matrix));      // set 42 phoneme to result
        return result;
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
