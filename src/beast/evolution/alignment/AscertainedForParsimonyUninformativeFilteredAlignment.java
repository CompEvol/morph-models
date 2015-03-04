package beast.evolution.alignment;

import beast.core.Input;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexandra Gavryushkina
 */
public class AscertainedForParsimonyUninformativeFilteredAlignment extends FilteredAlignment {

    public Input<Integer> excludefromNonConstantInput = new Input<Integer>("excludefromNonConstant", "first site to condition " +
            "on non constant parsimony uninformative sites, default 0", 0);
    public Input<Integer> excludetoNonConstantInput = new Input<Integer>("excludetoNonConstant", "last site to condition on " +
            "non constant parsimony uninformative sites (but excluding this site), default 0", 0);

    Set<Integer> excludedNonConstantPatterns; //one state differs only patterns

    @Override
    void setupAscertainment() {
        isAscertained = isAscertainedInput.get();

        if (isAscertained) {
            //From AscertainedAlignment
            int iFrom = excludefromInput.get();
            int iTo = excludetoInput.get();
            int iEvery = excludeeveryInput.get();
            excludedPatterns = new HashSet<Integer>();
            excludedNonConstantPatterns = new HashSet<Integer>();
            for (int i = iFrom; i < iTo; i += iEvery) {
                int iPattern = patternIndex[i];
                // reduce weight, so it does not confuse the tree likelihood
                patternWeight[iPattern] = 0;
                excludedPatterns.add(iPattern);
            }
            int iFromNC = excludefromNonConstantInput.get();
            int iToNC = excludetoNonConstantInput.get();
            for (int i = iFromNC; i < iToNC; i ++) {
                int iPattern = patternIndex[i];
                // reduce weight, so it does not confuse the tree likelihood
                patternWeight[iPattern] = 0;
                excludedNonConstantPatterns.add(iPattern);


            }
        }

    } // initAndValidate

    @Override
    public double getAscertainmentCorrection(double[] patternLogProbs) {
        double excludeProb = 0, includeProb = 0, returnProb = 1.0;

        for (int i : excludedPatterns) {
            excludeProb += Math.exp(patternLogProbs[i]);
        }

        int n = userDataTypeInput.get().getStateCount();
        for (int i : excludedNonConstantPatterns) {
            excludeProb += Math.exp(patternLogProbs[i])*n*(n-1);
        }

        if (includeProb == 0.0) {
            returnProb -= excludeProb;
        } else if (excludeProb == 0.0) {
            returnProb = includeProb;
        } else {
            returnProb = includeProb - excludeProb;
        }
        return Math.log(returnProb);
    } // getAscertainmentCorrection


}
