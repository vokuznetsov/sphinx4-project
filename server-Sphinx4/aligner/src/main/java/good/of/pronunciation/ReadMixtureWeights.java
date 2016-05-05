package good.of.pronunciation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author vkuzn on 05.05.2016.
 */
public class ReadMixtureWeights {

    private static final Logger log = LoggerFactory.getLogger(ReadMixtureWeights.class);
    private final String filename = "/models/acoustic/16khz/mixture_weights.txt";

    public ReadMixtureWeights() {
    }

    public Map<Integer, List<Double>> parseFile() throws IOException {
        Map<Integer, List<Double>> mixw = new HashMap<>();

        BufferedReader in = new BufferedReader(new InputStreamReader(openFile(filename)));
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
                }

                param = Integer.parseInt(str.substring(closeBracket - 1, closeBracket));
                if (param == 0) {
                    if (!mixw.isEmpty()){
                        mixw.get(count).addAll(new ArrayList<>(values));
                        values.clear();
                    }
                    count++;
                }

            } else if (str.equals("")) {
            } else {
                if (mixw.get(count) == null) {
                    mixw.put(count, new ArrayList<>(values));
                } else {
                    mixw.get(count).addAll(new ArrayList<>(values));
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
        mixw.get(count).addAll(new ArrayList<>(values));
        values.clear();

        Map<Integer, List<Double>> result = new HashMap<>();

        // разбиваем считанные значения на три части и суммируем их. - Не уверен в точности этого подхода.
        // divide our mixture weights on three list and sum them. It means, that I resize our mix. weights from 5126x384 to 5126x128
        for (int i=0; i <= count; i++){
            List<Double> firstParam = mixw.get(i).subList(0, 128);
            List<Double> secondParam = mixw.get(i).subList(128, 256);
            List<Double> thirdParam = mixw.get(i).subList(256, 384);

            List<Double> sum = IntStream.range(0, firstParam.size())
                    .mapToObj(j -> firstParam.get(j) + secondParam.get(j) + thirdParam.get(j))
                    .collect(Collectors.toList());
            result.put(i, new ArrayList<>(sum));
        }

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
