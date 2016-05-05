package good.of.pronunciation;

import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private Map<Integer, List<Double>> mixw;

    public GOP(List<WordResult> wordResults, Map<TimeFrame, Map<Integer, List<Double>>> mfccFeaturesForPhonemes,
               MDEF mdef, Map<Integer, Map<Integer, List<Double>>> means, Map<Integer, List<Double>> mixw) {

        this.wordResults = wordResults;
        this.mfccFeaturesForPhonemes = mfccFeaturesForPhonemes;
        this.mdef = mdef;
        this.means = means;
        this.mixw = mixw;
    }

    public double numerator() {

        TimeFrame timeFrame = wordResults.get(0).getTimeFrame();
        String phoneme = wordResults.get(0).getWord().toString().toUpperCase();

        // 10 - because phoneme "D" is a 10 number in means
        Map<Integer, List<Double>> vectorMeans = means.get(10);
        double[] mean = vectorMeans.get(0).stream().mapToDouble(Double::doubleValue).toArray();

        int start = (int) timeFrame.getStart();
        List<Double> segment = mfccFeaturesForPhonemes.get(timeFrame).get(start);
        double[] seg = segment.stream().mapToDouble(Double::doubleValue).toArray();

        //double[][] one = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}, {6.0}, {7.0}, {8.0}, {9.0}, {10.0}};

        RealMatrix realMatrix = new Array2DRowRealMatrix(2,39);
        realMatrix.setRow(0, seg);
        realMatrix.setRow(1, mean);
        Covariance covariance = new Covariance(realMatrix);
        double[][] data = covariance.getCovarianceMatrix().getData();

        //RealMatrix realMatrix= covariance.getCovarianceMatrix();
        //double[][] data = RealMatrix.getData();
//        double cov =covariance.covariance(three, four);

        MultivariateNormalDistribution distribution = new MultivariateNormalDistribution(mean, data);
        double result = distribution.density(seg);

        log.info("GOP");
        return 0.0;
    }

    public List<WordResult> getWordResults() {
        return wordResults;
    }

    public void setWordResults(List<WordResult> wordResults) {
        this.wordResults = wordResults;
    }

    public Map<TimeFrame, Map<Integer, List<Double>>> getMfccFeaturesForPhonemes() {
        return mfccFeaturesForPhonemes;
    }

    public void setMfccFeaturesForPhonemes(Map<TimeFrame, Map<Integer, List<Double>>> mfccFeaturesForPhonemes) {
        this.mfccFeaturesForPhonemes = mfccFeaturesForPhonemes;
    }

    public MDEF getMdef() {
        return mdef;
    }

    public void setMdef(MDEF mdef) {
        this.mdef = mdef;
    }

    public Map<Integer, Map<Integer, List<Double>>> getMeans() {
        return means;
    }

    public void setMeans(Map<Integer, Map<Integer, List<Double>>> means) {
        this.means = means;
    }

    public Map<Integer, List<Double>> getMixw() {
        return mixw;
    }

    public void setMixw(Map<Integer, List<Double>> mixw) {
        this.mixw = mixw;
    }
}
