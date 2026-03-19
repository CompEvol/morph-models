package morphmodels.evolution.substitutionmodel;

import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.spec.evolution.substitutionmodel.BasicGeneralSubstitutionModel;

/**
 * @author Luke Maurits
 */
@Description("A simple subclass of BasicGeneralSubstitutionModel which does not require a rates input and does require a number of states input.")
public abstract class NStatesNoRatesSubstitutionModel extends BasicGeneralSubstitutionModel {

    // Number of states input is required
    public Input<Integer> nrOfStatesInput = new Input<Integer>("stateNumber", "the number of character states", Validate.REQUIRED);

    @Override
    public void initAndValidate() {
        nrOfStates = nrOfStatesInput.get();
        frequencies = frequenciesInput.get();

        if(frequencies.getFreqs().length != nrOfStates) {
            throw new RuntimeException("number of stationary frequencies does not match number of states.");
        }

        updateMatrix = true;

        try {
            eigenSystem = createEigenSystem();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        rateMatrix = new double[nrOfStates][nrOfStates];
        relativeRates = new double[nrOfStates * (nrOfStates - 1)];
        storedRelativeRates = new double[nrOfStates * (nrOfStates - 1)];
    }
}
