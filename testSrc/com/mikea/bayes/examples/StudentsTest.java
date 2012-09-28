package com.mikea.bayes.examples;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Factor;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.VarSet;
import org.gga.graph.MutableGraph;
import org.gga.graph.impl.SparseGraphImpl;
import org.junit.Test;

import static com.mikea.bayes.Factor.newFactor;
import static org.junit.Assert.assertEquals;

/**
 * Applying library to Students example from Koller
 *
 * @author mike.aizatsky@gmail.com
 */
public class StudentsTest {
    public static final int D = 0;
    public static final int I = 1;
    public static final int G = 2;
    public static final int S = 3;
    public static final int L = 4;

    private static BayesianNetwork buildNetwork() {
        // D, I, G, S, L
        ProbabilitySpace space = new ProbabilitySpace(5, new int[] {2, 2, 3, 2, 2});

        MutableGraph g = new SparseGraphImpl(5, true);

        g.insert(D, G);
        g.insert(I, G);
        g.insert(I, S);
        g.insert(G, L);

        Factor factor0 = newFactor(space, new int[]{D}, new double[]{0.6, 0.4});
        Factor factor1 = newFactor(space, new int[]{I}, new double[]{0.7, 0.3});
        Factor factor2 = newFactor(space, new int[]{G, D, I}, new double[]{0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5, 0.3, 0.2});
        Factor factor3 = newFactor(space, new int[]{S, I}, new double[]{0.95, 0.05, 0.2, 0.8});
        Factor factor4 = newFactor(space, new int[]{L, G}, new double[]{0.1, 0.9, 0.4, 0.6, 0.99, 0.01});

        // check some factor values
        assertEquals(0.3, factor2.getValue(new int[]{0, 0, 0, -1, -1}), 1e-5);
        assertEquals(0.4, factor2.getValue(new int[]{0, 0, 1, -1, -1}), 1e-5);
        assertEquals(0.3, factor2.getValue(new int[]{0, 0, 2, -1, -1}), 1e-5);

        assertEquals(0.95, factor3.getValue(new int[]{-1, 0, -1, 0, -1}), 1e-5);
        assertEquals(0.05, factor3.getValue(new int[]{-1, 0, -1, 1, -1}), 1e-5);

        return new BayesianNetwork(space, g, new Factor[] {factor0, factor1, factor2, factor3, factor4});
    }

    @Test
    public void testValidate() throws Exception {
        BayesianNetwork n = buildNetwork();
        n.validate();
    }

    @Test
    public void testComputeProbability() throws Exception {
        BayesianNetwork n = buildNetwork();
        assertEquals(0.004608, n.computeProbability(new int[]{0, 1, 1, 1, 0}), 1e-5);
    }

    @Test
    public void testJointDistribution() throws Exception {
        BayesianNetwork n = buildNetwork();
        Factor f = n.computeJointDistribution();

        // All the values below are cross checked with text at pp 54-55

        assertEquals(0.004608, f.getValue(new int[]{0, 1, 1, 1, 0}), 1e-5);

        VarSet marginalizedL = f.getVarSet().removeVars(new int[]{L});
        VarSet marginalizedI = f.getVarSet().removeVars(new int[]{I});
        VarSet marginalizedD = f.getVarSet().removeVars(new int[]{D});

        // Compute P(L)
        Factor lDistribution = f.marginalize(marginalizedL);
        assertEquals(
                "Factor({4}, [0.49766399999999994, 0.502336])",
                lDistribution.toString());

        // Compute P(L|i0)
        assertEquals(
                "Factor({4}, [0.6114, 0.3886])",
                f.observeEvidence(new int[]{I}, new int[]{0}).marginalize(marginalizedL).normalize().toString());

        // Compute P(L|i0, d0)
        assertEquals(
                "Factor({4}, [0.48699999999999993, 0.5130000000000001])",
                f.observeEvidence(new int[]{I, D}, new int[]{0, 0}).marginalize(marginalizedL).normalize().toString());

        // Compute P(I)
        assertEquals(
                "Factor({1}, [0.7, 0.3])",
                f.marginalize(marginalizedI).normalize().toString());

        // Compute P(I|g3), P(D|g3)
        assertEquals(
                "Factor({1}, [0.9210526315789473, 0.07894736842105267])",
                f.observeEvidence(new int[]{G}, new int[]{2}).marginalize(marginalizedI).normalize().toString());
        assertEquals(
                "Factor({0}, [0.37070938215102983, 0.6292906178489702])",
                f.observeEvidence(new int[]{G}, new int[]{2}).marginalize(marginalizedD).normalize().toString());

        // Compute P(I|l0), P(I|g3, l0), P(I|g3, s1)
        assertEquals(
                "Factor({1}, [0.8599778163580247, 0.14002218364197533])",
                f.observeEvidence(new int[]{L}, new int[]{0}).marginalize(marginalizedI).normalize().toString());
        assertEquals(
                "Factor({1}, [0.9210526315789473, 0.07894736842105265])",
                f.observeEvidence(new int[]{G, L}, new int[]{2, 0}).marginalize(marginalizedI).normalize().toString());
        assertEquals(
                "Factor({1}, [0.4216867469879517, 0.5783132530120483])",
                f.observeEvidence(new int[]{G, S}, new int[]{2, 1}).marginalize(marginalizedI).normalize().toString());
    }
}
