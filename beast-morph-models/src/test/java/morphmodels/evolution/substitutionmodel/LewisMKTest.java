package morphmodels.evolution.substitutionmodel;

import beast.base.evolution.tree.Node;
import beast.base.spec.evolution.substitutionmodel.Frequencies;
import beast.base.spec.inference.parameter.SimplexParam;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LewisMKTest {

    @Test
    void testLewisMKInitWithStateNumber() {
        LewisMK model = new LewisMK();
        model.initByName("stateNumber", 4);

        assertEquals(4, model.getStateCount());

        double[] freqs = model.getFrequencies();
        assertEquals(4, freqs.length);
        for (double f : freqs) {
            assertEquals(0.25, f, 1e-10);
        }
    }

    @Test
    void testTransitionProbabilitiesRowsSumToOne() {
        LewisMK model = new LewisMK();
        model.initByName("stateNumber", 4);

        double[] matrix = new double[16];
        Node node = new Node();
        model.getTransitionProbabilities(node, 1.0, 0.0, 1.0, matrix);

        for (int i = 0; i < 4; i++) {
            double rowSum = 0;
            for (int j = 0; j < 4; j++) {
                rowSum += matrix[i * 4 + j];
                assertTrue(matrix[i * 4 + j] >= 0, "Transition probability should be non-negative");
            }
            assertEquals(1.0, rowSum, 1e-10, "Row " + i + " should sum to 1.0");
        }
    }

    @Test
    void testTransitionProbabilitiesAtZeroTime() {
        LewisMK model = new LewisMK();
        model.initByName("stateNumber", 3);

        double[] matrix = new double[9];
        Node node = new Node();
        // zero branch length => identity matrix
        model.getTransitionProbabilities(node, 1.0, 1.0, 1.0, matrix);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    assertEquals(1.0, matrix[i * 3 + j], 1e-10);
                } else {
                    assertEquals(0.0, matrix[i * 3 + j], 1e-10);
                }
            }
        }
    }

    @Test
    void testWithProvidedFrequencies() {
        LewisMK model = new LewisMK();
        Frequencies freqs = new Frequencies();
        freqs.initByName("frequencies", new SimplexParam(new double[]{0.1, 0.2, 0.3, 0.4}));

        model.initByName("stateNumber", 4, "frequencies", freqs);

        double[] modelFreqs = model.getFrequencies();
        assertEquals(0.1, modelFreqs[0], 1e-10);
        assertEquals(0.2, modelFreqs[1], 1e-10);
        assertEquals(0.3, modelFreqs[2], 1e-10);
        assertEquals(0.4, modelFreqs[3], 1e-10);

        // transition probabilities should still sum to 1.0
        double[] matrix = new double[16];
        Node node = new Node();
        model.getTransitionProbabilities(node, 1.0, 0.0, 1.0, matrix);

        for (int i = 0; i < 4; i++) {
            double rowSum = 0;
            for (int j = 0; j < 4; j++) {
                rowSum += matrix[i * 4 + j];
            }
            assertEquals(1.0, rowSum, 1e-10, "Row " + i + " should sum to 1.0");
        }
    }

    @Test
    void testOrdinalRateMatrixIsTridiagonal() {
        Ordinal model = new Ordinal();
        Frequencies freqs = new Frequencies();
        freqs.initByName("frequencies", new SimplexParam(new double[]{0.2, 0.2, 0.2, 0.2, 0.2}));
        model.initByName("stateNumber", 5, "frequencies", freqs);

        // trigger rate setup
        double[] matrix = new double[25];
        Node node = new Node();
        model.getTransitionProbabilities(node, 1.0, 0.0, 1.0, matrix);

        // verify transition probabilities sum to 1.0
        for (int i = 0; i < 5; i++) {
            double rowSum = 0;
            for (int j = 0; j < 5; j++) {
                rowSum += matrix[i * 5 + j];
            }
            assertEquals(1.0, rowSum, 1e-10, "Row " + i + " should sum to 1.0");
        }

        // check relative rates are tridiagonal
        double[] rates = model.getRelativeRates();
        // relativeRates[0] = rate(0->1) = 1.0
        assertEquals(1.0, rates[0], 1e-10);
        // relativeRates[nrOfStates*(nrOfStates-1)-1] = rate(4->3) = 1.0
        assertEquals(1.0, rates[19], 1e-10);
    }

    @Test
    void testNestedOrdinalRateMatrix() {
        NestedOrdinal model = new NestedOrdinal();
        Frequencies freqs = new Frequencies();
        freqs.initByName("frequencies", new SimplexParam(new double[]{0.2, 0.2, 0.2, 0.2, 0.2}));
        model.initByName("stateNumber", 5, "frequencies", freqs);

        // trigger rate setup
        double[] matrix = new double[25];
        Node node = new Node();
        model.getTransitionProbabilities(node, 1.0, 0.0, 1.0, matrix);

        // verify transition probabilities sum to 1.0
        for (int i = 0; i < 5; i++) {
            double rowSum = 0;
            for (int j = 0; j < 5; j++) {
                rowSum += matrix[i * 5 + j];
            }
            assertEquals(1.0, rowSum, 1e-10, "Row " + i + " should sum to 1.0");
        }

        // check relative rates: first row should have all 1s (state 0 can go to any state)
        double[] rates = model.getRelativeRates();
        for (int i = 0; i < 4; i++) {
            assertEquals(1.0, rates[i], 1e-10, "Rate from state 0 to state " + (i+1) + " should be 1.0");
        }
    }

    @Test
    void testRateMatrixEqualFrequencies() {
        LewisMK model = new LewisMK();
        model.initByName("stateNumber", 4);

        Node node = new Node();
        double[] rateMatrix = model.getRateMatrix(node);
        assertEquals(16, rateMatrix.length);

        // all off-diagonal should be 1/(n-1) = 1/3
        // all diagonal should be -1.0
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j) {
                    assertEquals(-1.0, rateMatrix[i * 4 + j], 1e-10);
                } else {
                    assertEquals(1.0 / 3.0, rateMatrix[i * 4 + j], 1e-10);
                }
            }
        }
    }

    @Test
    void testRejectsOneState() {
        LewisMK model = new LewisMK();
        assertThrows(RuntimeException.class, () ->
            model.initByName("stateNumber", 1)
        );
    }
}
