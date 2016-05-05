package good.of.pronunciation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vkuzn on 05.05.2016.
 */
public class ReadMixtureWeights {

    private static final Logger log = LoggerFactory.getLogger(ReadMixtureWeights.class);
    private final String mixw = "/models/acoustic/16khz/mixture_weights.txt";

    public ReadMixtureWeights() {
    }

    public Map<Integer, List<Double>> parseFile() throws IOException {
        Map<Integer, List<Double>> result = new HashMap<>();

        BufferedReader in = new BufferedReader(new InputStreamReader(openFile(mixw)));
        in.readLine();      // skip first string

        String str = null;
        int numberOfPhoneme = 0; // first value in mixw[5,2]. In this case, numberOfPhoneme = 5
        int param = 0;           // second value. In the example above, param = 2
        int count = -1;
        List<Double> values = new ArrayList<>();
        //Map<Integer, List<Double>> matrix = new HashMap<>();

        while ((str = in.readLine()) != null) {
            str = str.trim().replaceAll(" +", " ");
            if (str.contains("mixw")) {
                int openBracket = str.indexOf("[");
                int closeBracket = str.indexOf("]");
                int phoneme = Integer.parseInt(str.substring(openBracket + 1, openBracket + 2));

                if (numberOfPhoneme != phoneme) {
                    numberOfPhoneme = phoneme;
                    //result.put(count, new ArrayList<>(values));
                }

                param = Integer.parseInt(str.substring(closeBracket - 1, closeBracket));
                if (param == 0) {
                    if (!result.isEmpty()){
                        result.get(count).addAll(new ArrayList<>(values));
                        values.clear();
                    }
                    count++;
                }

            } else if (str.equals("")) {
            } else {
                if (result.get(count) == null) {
                    result.put(count, new ArrayList<>(values));
                } else {
                    result.get(count).addAll(new ArrayList<>(values));
                }
                values.clear();


                List<Integer> spaces = findAllCharacterInString(str, " ");

                for (int i = 0; i < spaces.size(); i++) {
                    if (i == 0) {
                        values.add(Double.valueOf(str.substring(i, spaces.get(i) + 1)));
                        values.add(Double.valueOf(str.substring(spaces.get(i) + 1, spaces.get(i + 1))));
                        String sss = "";
                    } else if (i == spaces.size() - 1)
                        values.add(Double.valueOf(str.substring(spaces.get(i) + 1, str.length())));
                    else
                        values.add(Double.valueOf(str.substring(spaces.get(i) + 1, spaces.get(i + 1))));
                }
            }
        }
        result.get(count).addAll(new ArrayList<>(values));
        values.clear();
        return result;
    }


    private InputStream openFile(String path) throws FileNotFoundException {
        InputStream is = this.getClass().getResourceAsStream(path);
        return is;
    }

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
