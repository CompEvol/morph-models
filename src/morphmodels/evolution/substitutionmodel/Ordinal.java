package morphmodels.evolution.substitutionmodel;

import beast.base.core.Citation;
import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.evolution.datatype.Binary;
import beast.base.evolution.datatype.DataType;
import beast.base.evolution.datatype.StandardData;
import beast.base.evolution.tree.Node;

import java.util.Arrays;
import beast.base.evolution.substitutionmodel.*;

/**
 * @author Luke Maurits
 */
@Description("Ordinal substitution model with equal relative rates.")
public class Ordinal extends NStatesNoRatesSubstitutionModel {

    @Override
    public void setupRelativeRates() {

    /**
     * A relative rate matrix for a model of this class looks as follows
     * (for, e.g. 5 states)
     *
     * [ 0 1 0 0 0 ]
     * [ 1 0 1 0 0 ]
     * [ 0 1 0 1 0 ]
     * [ 0 0 1 0 1 ]
     * [ 0 0 0 1 0 ]
     *
     * i.e. transitions are permitted only to states one position "above"
     * or "below" the current state.  This leaves two options for most
     * states, but only one for the "highest" and "lowest" states.
     */

    int i;
    // Initialise everything to zero
    for(i=0; i<nrOfStates * (nrOfStates -1); i++) {
        relativeRates[i] = 0;
    }
    // Low edge case (only one possible transition)
    relativeRates[0] = 1.0;
    // Standard cases (two possible transitions)
    for(i=1; i<nrOfStates-1; i++) {
        relativeRates[nrOfStates*i-1] = 1.0;
        relativeRates[nrOfStates*i] = 1.0;
    }
    // High edge case (only one possible transition)
    relativeRates[nrOfStates*(nrOfStates-1)-1] = 1.0;
    }

}
