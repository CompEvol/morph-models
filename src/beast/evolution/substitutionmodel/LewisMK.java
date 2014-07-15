package beast.evolution.substitutionmodel;

import beast.core.Input;
import beast.core.parameter.RealParameter;
import beast.evolution.datatype.Binary;
import beast.evolution.datatype.DataType;
import beast.evolution.datatype.Nucleotide;
import beast.evolution.datatype.StandardData;
import beast.evolution.tree.Node;

import java.util.Arrays;

/**
 * @author Alexandra Gavryushkina
 */
public class LewisMK extends SubstitutionModel.Base  {

    public Input<Integer> nrOfStatesInput = new Input<Integer>("stateNumber", "the number of character states", Input.Validate.REQUIRED);

    public LewisMK() {
        // this is added to avoid a parsing error inherited from superclass because frequencies are not provided.
        frequenciesInput.setRule(Input.Validate.OPTIONAL);
        try {
            // this call will be made twice when constructed from XML
            // but this ensures that the object is validly constructed for testing purposes.
            initAndValidate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("initAndValidate() call failed when constructing LewisMK()");
        }
    }

    double[] frequencies;
    EigenDecomposition eigenDecomposition;

    @Override
    public void initAndValidate() throws Exception {

        nrOfStates = nrOfStatesInput.get();

//        double[] eval = new double[]{0.0, -1.3333333333333333, -1.3333333333333333, -1.3333333333333333};
//        double[] evec = new double[]{1.0, 2.0, 0.0, 0.5, 1.0, -2.0, 0.5, 0.0, 1.0, 2.0, 0.0, -0.5, 1.0, -2.0, -0.5, 0.0};
//        double[] ivec = new double[]{0.25, 0.25, 0.25, 0.25, 0.125, -0.125, 0.125, -0.125, 0.0, 1.0, 0.0, -1.0, 1.0, 0.0, -1.0, 0.0};
//
//        eigenDecomposition = new EigenDecomposition(evec, ivec, eval);
        frequencies = new double[nrOfStates];
        Arrays.fill(frequencies, (1/nrOfStates));
    }

    @Override
    public double[] getFrequencies() {
        return frequencies;
    }

    @Override
    public void getTransitionProbabilities(Node node, double fStartTime, double fEndTime, double fRate, double[] matrix) {
        double fDelta = (nrOfStates/(nrOfStates-1)) * (fStartTime - fEndTime);
        double fPStay = (1.0 + (nrOfStates -1) * Math.exp(-fDelta * fRate)) / nrOfStates;
        double fPMove = (1.0 - Math.exp(-fDelta * fRate)) / nrOfStates;
        // fill the matrix with move probabilities
        Arrays.fill(matrix, fPMove);
        // fill the diagonal
        for (int i = 0; i < nrOfStates; i++) {
            matrix[i * (nrOfStates+1)] = fPStay;
        }
    }

    @Override
    public EigenDecomposition getEigenDecomposition(Node node) {
        return eigenDecomposition;
    }

    @Override
    public boolean canHandleDataType(DataType dataType) throws Exception {
        if (dataType instanceof StandardData || dataType instanceof Binary) {
            return true;
        }
        throw new Exception("Can only handle nucleotide data");
    }

}
