package beast.evolution.substitutionmodel;

import beast.core.Citation;
import beast.core.Description;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.parameter.RealParameter;
import beast.evolution.datatype.Binary;
import beast.evolution.datatype.DataType;
import beast.evolution.datatype.StandardData;
import beast.evolution.tree.Node;

import java.util.Arrays;

/**
 * @author Luke Maurits
 */
@Description("Ordinal substitution model with equal relative rates and a special \"out of system\" state.")
public class NestedOrdinal extends NStatesNoRatesSubstitutionModel {

    @Override
    protected void setupRelativeRates() {

    /**
     * A relative rate matrix for a model of this class looks as follows
     * (for, e.g. 5 states)
     *
     * [ 0 1 1 1 1 ]
     * [ 1 0 1 0 0 ]
     * [ 1 1 0 1 0 ]
     * [ 1 0 1 0 1 ]
     * [ 1 0 0 1 0 ]
     *
     * Note that there is an n-1 state Ordinal rate matrix nested in the
     * bottom right of an n state NestedOrdinal rate matrix, hence the name.
     * Transitions amongst the n-1 "highest" states are essentially as per
     * the Ordinal model.  The "lowest" state is special in that it can
     * transition to any state and any state can transition to it.  This
     * permits modelling some morphological or linguistic trait which
     * has a natural scale but can also be lost outright without sliding
     * down the scale, or can be gained in some form other than the most
     * basic.
     */

    int i;
    // Initialise everything to zero
    for(i=0; i<nrOfStates * (nrOfStates -1); i++) {
        relativeRates[i] = 0;
    }
    // Top row - "getting out of jail"
    // All "within system" states equally likely
    for(i=0; i<nrOfStates-1; i++) {
        relativeRates[i] = 1.0;
    }
    // Leftmost colum - "getting into jail"
    for(i=1; i<nrOfStates; i++) {
        relativeRates[i*(nrOfStates-1)] = 1.0;
    }
    // Low edge case (only one possible transition)
    relativeRates[nrOfStates] = 1.0;
    // Standard cases (two possible transitions)
    for(i=2; i<nrOfStates-1; i++) {
        relativeRates[nrOfStates*i-1] = 1.0;
        relativeRates[nrOfStates*i] = 1.0;
    }
    // High edge case (only one possible transition)
    relativeRates[nrOfStates*(nrOfStates-1)-1] = 1.0;
    }

}
