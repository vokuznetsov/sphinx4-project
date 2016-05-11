package aligner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vkuzn on 11.05.2016.
 */
public class Main1 {
    public static void main(String[] args){

        List<Double> list = new ArrayList<>();

        for (int i = 1; i< 6; i++) {
            list.add(Double.valueOf(i));
        }

        System.out.println(list.toString());

        double result = list.stream().reduce((aDouble, aDouble2) -> aDouble*aDouble2).orElse(0.0);

        System.out.println(result);

    }
}
