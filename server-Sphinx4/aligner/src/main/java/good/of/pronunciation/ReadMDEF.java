package good.of.pronunciation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vkuzn on 18.04.2016.
 */
public class ReadMDEF {

    private static final Logger log = LoggerFactory.getLogger(ReadMDEF.class);

    /**
     * This method reflect mdef file in MDEF class.
     */
    public MDEF readMDEF() throws IOException {
        String file = "/models/acoustic/16khz/mdef";
        BufferedReader in = new BufferedReader(new InputStreamReader(openFile(file)));

        // skip first 10 string in mdef file, because they are unnecessary for us.
        for (int i = 0; i < 10; i++) {
            in.readLine();
        }

        String str;
        int count = 1;
        MDEF mdef = new MDEF();
        while ((str = in.readLine()) != null) {

            // replace 2 or more space with single space
            str = str.trim().replaceAll(" +", " ");
            List<Integer> spaces = findAllCharacterInString(str, " ");

            String base = str.substring(0, spaces.get(0));
//            String left = str.substring(spaces.get(0) + 1, spaces.get(1));
//            String right = str.substring(spaces.get(1) + 1, spaces.get(2));
            String baseLeftRight = str.substring(0, spaces.get(2));
            String p = str.substring(spaces.get(2) + 1, spaces.get(3));

            // space.get(3) - space.get(4) - field attrib in mdef (this field is unnecessary for us)
            Integer tmat = Integer.valueOf(str.substring(spaces.get(4) + 1, spaces.get(5)));
            List<Integer> stateId = new ArrayList<>();
            stateId.add(Integer.valueOf(str.substring(spaces.get(5) + 1, spaces.get(6))));
            stateId.add(Integer.valueOf(str.substring(spaces.get(6) + 1, spaces.get(7))));
            stateId.add(Integer.valueOf(str.substring(spaces.get(7) + 1, spaces.get(8))));

//            mdef.getBase().put(count, base);
//            mdef.getLeft().put(count, left);
//            mdef.getRight().put(count, right);
            mdef.getBaseLeftRight().put(baseLeftRight, count);
            mdef.getP().put(count, p);
            mdef.getTmat().put(count, tmat);
            mdef.getStateId().put(count, stateId);

            if (count <= 42)
                MDEF.getBaseCorrespondTmat().put(base, count-1);
            count++;
        }
        return mdef;
    }

    /**
     * @param path - path to mdef file
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
