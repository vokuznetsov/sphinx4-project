package good.of.pronunciation;

import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    Map<Integer, String> timeOfPhonemes;

    public GOP(List<WordResult> wordResults, Map<TimeFrame, Map<Integer, List<Double>>> mfccFeaturesForPhonemes,
               MDEF mdef, Map<Integer, Map<Integer, List<Double>>> means,
               Map<Integer, Map<Integer, List<Double>>> variances, Map<Integer, List<Double>> mixw) {

        this.wordResults = wordResults;
        this.mfccFeaturesForPhonemes = mfccFeaturesForPhonemes;
        this.mdef = mdef;
        this.means = means;
        this.variances = variances;
        this.mixw = mixw;

        this.timeOfPhonemes = getTimeOfPhonemes();
    }

    public List<Double> numerator() {

        List<Double> probability = new ArrayList<>();

        for (int number = 0; number < wordResults.size(); number++) {
            TimeFrame timeFrame = wordResults.get(number).getTimeFrame();

            for (int i = (int) timeFrame.getStart(); i <= timeFrame.getEnd(); i += 10) {
                String phoneme = wordResults.get(number).getWord().toString().toUpperCase();
                int numberOfPhoneme = MDEF.getBaseCorrespondTmat().get(phoneme);

                List<Double> segment = mfccFeaturesForPhonemes.get(timeFrame).get(i);
                double[] seg = segment.stream().mapToDouble(Double::doubleValue).toArray();

                Map<Integer, List<Double>> vectorMeans = means.get(numberOfPhoneme);
                Map<Integer, List<Double>> vectorVariances = variances.get(numberOfPhoneme);

                double result = 0.0;
                List<Double> mixtureWeight = getMixtureWeightsForNumerator(number, i);

                // comp = number of component Gaussian density; comp=1..128
                for (int comp = 0; comp < means.get(numberOfPhoneme).size(); comp++) {

                    double[] mean = vectorMeans.get(comp).stream().mapToDouble(Double::doubleValue).toArray();
                    double[][] covariance = getCovarianceMatrix(vectorVariances.get(comp));

                    try {
                        MultivariateNormalDistribution distribution = new MultivariateNormalDistribution(mean, covariance);
                        result += mixtureWeight.get(comp) * distribution.density(seg);
                        //result += distribution.density(seg);
                    } catch (Exception e) {
                        log.info("matrix is singular");
                    }
                }
                probability.add(result);
            }
        }

        return probability;
    }

    public List<Double> denominator() {

        int count = -1;
        List<Double> maxProbability = new ArrayList<>();

        for (int number = 0; number < wordResults.size(); number++) {
            TimeFrame timeFrame = wordResults.get(number).getTimeFrame();

            for (int i = (int) timeFrame.getStart(); i <= timeFrame.getEnd();  i += 10) {

                count++;
                List<Double> segment = mfccFeaturesForPhonemes.get(timeFrame).get(i);
                double[] seg = segment.stream().mapToDouble(Double::doubleValue).toArray();

                for (int j = 0; j < MDEF.getTmatStateId().size(); j++) {

                    Map<Integer, List<Double>> vectorMeans = means.get(j);
                    Map<Integer, List<Double>> vectorVariances = variances.get(j);

                    for (Integer mixWeight: MDEF.getTmatStateId().get(j)) {

                        double result = 0.0;
                        List<Double> mixtureWeight = mixw.get(mixWeight);

                        for (int comp = 0; comp < means.get(j).size(); comp++) {

                            double[] mean = vectorMeans.get(comp).stream().mapToDouble(Double::doubleValue).toArray();
                            double[][] covariance = getCovarianceMatrix(vectorVariances.get(comp));

                            try {
                                MultivariateNormalDistribution distribution = new MultivariateNormalDistribution(mean, covariance);
                                result += mixtureWeight.get(comp) * distribution.density(seg);
                            } catch (Exception e) {
                                log.info("matrix is singular for " + i + " from " + timeFrame.getEnd() + ". Phoneme is " + wordResults.get(number).getWord().toString());
                            }
                        }

                        if (count != maxProbability.size() - 1)
                            maxProbability.add(result);
                        else if (maxProbability.get(count) < result) {
                            maxProbability.set(count, result);
                        }
                    }
                }
            }
        }


        return maxProbability;
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

    private Map<Integer, String> getTimeOfPhonemes() {

        Map<Integer, String> result = new HashMap<>();

        for (WordResult wordResult : wordResults) {

            String phoneme = wordResult.getWord().toString().toUpperCase();
            int start = (int) wordResult.getTimeFrame().getStart();
            int end = (int) wordResult.getTimeFrame().getEnd();

            for (int i = start; i <= end; i += 10)
                result.put(i, phoneme);
        }

        return result;
    }

    private List<Double> getMixtureWeightsForNumerator(int numberOfWordList, int time) {

        String base = wordResults.get(numberOfWordList).getWord().toString().toUpperCase();
        String left;
        String right;
        String baseLeftRight;

        if (timeOfPhonemes.containsKey(time - 10)) {
            left = timeOfPhonemes.get(time - 10);
        } else left = "SIL";

        if (timeOfPhonemes.containsKey(time + 10)) {
            right = timeOfPhonemes.get(time + 10);
        } else right = "SIL";

        baseLeftRight = base + " " + left + " " + right;
        int number = mdef.getBaseLeftRight().get(baseLeftRight);

        //int tmat = mdef.getTmat().get(number);
        List<Integer> stateId = mdef.getStateId().get(number);

        return mixw.get(stateId.get(0));
    }
}
