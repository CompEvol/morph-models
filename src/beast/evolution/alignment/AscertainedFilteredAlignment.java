package beast.evolution.alignment;

import beast.core.Input;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by agav755 on 3/09/14.
 */
public class AscertainedFilteredAlignment extends FilteredAlignment {

    public Input<Integer> excludefromInput = new Input<Integer>("excludefrom", "first site to condition on, default 0", 0);
    public Input<Integer> excludetoInput = new Input<Integer>("excludeto", "last site to condition on (but excluding this site), default 0", 0);
    public Input<Integer> excludeeveryInput = new Input<Integer>("excludeevery", "interval between sites to condition on (default 1)", 1);

    /**
     * indices of patterns that are excluded from the likelihood calculation
     * and used for ascertainment correction
     */
    Set<Integer> excludedPatterns;

    @Override
    public void initAndValidate() throws Exception {
        super.initAndValidate();

        int iFrom = excludefromInput.get();
        int iTo = excludetoInput.get();
        int iEvery = excludeeveryInput.get();
        excludedPatterns = new HashSet<Integer>();
        for (int i = iFrom; i < iTo; i += iEvery) {
            int iPattern = patternIndex[i];
            // reduce weight, so it does not confuse the tree likelihood
            patternWeight[iPattern] = 0;
            excludedPatterns.add(iPattern);
        }

    } // initAndValidate

    public double getAscertainmentCorrection(double[] patternLogProbs) {
        double excludeProb = 0.0;

        for (int i : excludedPatterns) {
            excludeProb += Math.exp(patternLogProbs[i]);
        }

        return Math.log(1 - excludeProb);
    } // getAscertainmentCorrection

}
