package good.of.pronunciation;

import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author vkuzn on 06.05.2016.
 */
public class GOP {

    private static final Logger log = LoggerFactory.getLogger(GOP.class);

    private List<WordResult> wordResults;
    private Map<TimeFrame, Map<Integer, List<Double>>> mfccFeaturesForPhonemes;

    private MDEF mdef;
    private Map<Integer, Map<Integer, List<Double>>> means;
    private Map<Integer, Map<Integer, List<Double>>> variances;
    private Map<Integer, List<Double>> mixw;

    public GOP(List<WordResult> wordResults, Map<TimeFrame, Map<Integer, List<Double>>> mfccFeaturesForPhonemes,
               MDEF mdef, Map<Integer, Map<Integer, List<Double>>> means,
               Map<Integer, Map<Integer, List<Double>>> variances, Map<Integer, List<Double>> mixw) {

        this.wordResults = wordResults;
        this.mfccFeaturesForPhonemes = mfccFeaturesForPhonemes;
        this.mdef = mdef;
        this.means = means;
        this.variances = variances;
        this.mixw = mixw;
    }

    public double numerator() {

        List<Double> probability = new ArrayList<>();

        for (WordResult wordResult : wordResults) {
            TimeFrame timeFrame = wordResult.getTimeFrame();

            for (int i = (int) timeFrame.getStart(); i <= timeFrame.getEnd(); i += 10) {
                String phoneme = wordResult.getWord().toString().toUpperCase();
                int numberOfPhoneme = MDEF.getBaseCorrespondTmat().get(phoneme);

                List<Double> segment = mfccFeaturesForPhonemes.get(timeFrame).get(i);
                double[] seg = segment.stream().mapToDouble(Double::doubleValue).toArray();

                Map<Integer, List<Double>> vectorMeans = means.get(numberOfPhoneme);
                Map<Integer, List<Double>> vectorVariances = variances.get(numberOfPhoneme);
                double result = 0.0;
                double mixtureWeight = 1.0;

                // comp = number of component Gaussian density; comp=1..128
                for (int comp = 0; comp < means.get(numberOfPhoneme).size(); comp++) {

                    double[] mean = vectorMeans.get(comp).stream().mapToDouble(Double::doubleValue).toArray();
                    double[][] covariance = getCovarianceMatrix(vectorVariances.get(comp));

                    try {
                        MultivariateNormalDistribution distribution = new MultivariateNormalDistribution(mean, covariance);
                        result += mixtureWeight * distribution.density(seg);
                    } catch (Exception e) {
                        log.info("matrix is singular");
                    }
                }
                probability.add(result);
            }
        }


        log.info("GOP");
        return 0.0;
    }

    /**
     * create diagonal covariance matrxix. Diagonal is variances, other values is zero.
     */
    private double[][] getCovarianceMatrix(List<Double> variance) {
        double[][] covariance = new double[39][39];

        for (int i = 0; i < variance.size(); i++) {
            for (int j = 0; j < variance.size(); j++) {
                if (i == j) {
                    covariance[i][j] = variance.get(i);
                } else covariance[i][j] = 0;
            }
        }
        return covariance;
    }
}
