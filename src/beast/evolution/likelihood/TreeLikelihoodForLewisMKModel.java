package beast.evolution.likelihood;

import beast.evolution.alignment.AscertainedFilteredAlignment;
import beast.evolution.sitemodel.SiteModelInterface;
import beast.evolution.substitutionmodel.LewisMK;

/**
 * Alexandra Gavryushkina
 */
public class TreeLikelihoodForLewisMKModel extends TreeLikelihood {

    @Override
    public void initAndValidate() throws Exception {
        super.initAndValidate();
//        if (((LewisMK)((SiteModelInterface.Base) siteModelInput.get()).substModelInput.get()).isMKvInput.get()) {
//            useAscertainedSitePatterns = true;
//        }
        if (dataInput.get() instanceof AscertainedFilteredAlignment) {
            useAscertainedSitePatterns = true;
        }
    }

    void calcLogP() throws Exception {
        logP = 0.0;
        if (useAscertainedSitePatterns) {
            final double ascertainmentCorrection = ((AscertainedFilteredAlignment) dataInput.get()).getAscertainmentCorrection(patternLogLikelihoods);
            for (int i = 0; i < dataInput.get().getPatternCount(); i++) {
                logP += (patternLogLikelihoods[i] - ascertainmentCorrection) * dataInput.get().getPatternWeight(i);
            }
        } else {
            for (int i = 0; i < dataInput.get().getPatternCount(); i++) {
                logP += patternLogLikelihoods[i] * dataInput.get().getPatternWeight(i);
            }
        }
    }
}
