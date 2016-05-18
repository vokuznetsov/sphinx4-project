package aligner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author vkuzn on 11.05.2016.
 */
public class Main1 {
    private static final String pathToFolder = "C:\\Users\\vkuzn\\Documents\\IdeaProjects\\CourseWork\\" +
            "sphinx4-project\\server-Sphinx4\\aligner\\src\\main\\resources\\audio\\16kHz_16bit_native\\development";

    public static void main(String[] args){

//        List<Double> list = new ArrayList<>();
//
//        for (int i = 1; i < 6; i++) {
//            list.add(Double.valueOf(i));
//        }
//
//        System.out.println(list.toString());
//
//        double result = list.stream().reduce((aDouble, aDouble2) -> aDouble * aDouble2).orElse(0.0);
//
//        System.out.println(result);
//
//        List<Double> doubles1 = new ArrayList<>();
//        List<Double> doubles2 = new ArrayList<>();
//
//        for (int i = 0; i < 10; i++) {
//            doubles1.add(Double.valueOf(i + "." + i));
//            doubles2.add(Double.valueOf(i + "." + i));
//        }
//        List<Double> resultDoubles = IntStream.range(0, doubles1.size())
//                .mapToObj(k -> doubles1.get(k) * doubles2.get(k))
//                .collect(Collectors.toList());
//
//        System.out.println(resultDoubles.toString());
//        System.out.println(resultDoubles.stream().max(Double::compareTo).get());
//        double sum = resultDoubles.stream().mapToDouble(Double::doubleValue).sum();
//        System.out.println(sum);


        try {
            Files.walk(Paths.get(pathToFolder)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    //System.out.println(filePath.getParent().getFileName());
                    System.out.println(filePath.getFileName());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
