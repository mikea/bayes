package com.mikea.bayes.examples;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Factor;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarSet;
import org.junit.Test;

import static com.mikea.bayes.Factor.newFactor;
import static com.mikea.bayes.Var.newVar;
import static com.mikea.bayes.Var.vars;
import static com.mikea.bayes.VarAssignment.at;
import static org.junit.Assert.assertEquals;

/**
 * Applying library to Students example from Koller.
 *
 * @author mike.aizatsky@gmail.com
 */
public class StudentsTest {
    private static final Var D = newVar("D", 2);
    private static final Var I = newVar("I", 2);
    private static final Var G = newVar("G", 3);
    private static final Var S = newVar("S", 2);
    private static final Var L = newVar("L", 2);

    private static BayesianNetwork buildNetwork() {
        Factor factorD = newFactor(vars(D), new double[]{0.6, 0.4});
        Factor factorI = newFactor(vars(I), new double[]{0.7, 0.3});
        Factor factorG = newFactor(vars(G, D, I), new double[]{0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5, 0.3, 0.2});
        Factor factorS = newFactor(vars(S, I), new double[]{0.95, 0.05, 0.2, 0.8});
        Factor factorL = newFactor(vars(L, G), new double[]{0.1, 0.9, 0.4, 0.6, 0.99, 0.01});

        // check some factor values
        assertEquals(0.3,
                factorG.getValue(at(G, 0).at(D, 0).at(I, 0)),
                1e-5);
        assertEquals(0.4,
                factorG.getValue(at(G, 1).at(D, 0).at(I, 0)),
                1e-5);
        assertEquals(0.3,
                factorG.getValue(at(G, 2).at(D, 0).at(I, 0)),
                1e-5);

        assertEquals(0.95,
                factorS.getValue(at(S, 0).at(I, 0)), 1e-5);
        assertEquals(0.05,
                factorS.getValue(at(S, 1).at(I, 0)), 1e-5);

        return BayesianNetwork
                .withVariables(D, I, G, S, L)
                .edge(D, G)
                .edge(I, G)
                .edge(I, S)
                .edge(G, L)
                .factor(D, factorD)
                .factor(I, factorI)
                .factor(G, factorG)
                .factor(S, factorS)
                .factor(L, factorL)
                .build();
    }


    @Test
    public void testValidate() throws Exception {
        BayesianNetwork n = buildNetwork();
        n.validate();
    }

    @Test
    public void testComputeProbability() throws Exception {
        BayesianNetwork n = buildNetwork();
        assertEquals(0.004608, n.computeProbability(at(D, 0).at(I, 1).at(G, 1).at(S, 1).at(L, 0)), 1e-5);
    }

    @Test
    public void testJointDistribution() throws Exception {
        BayesianNetwork n = buildNetwork();
        Factor f = n.computeJointDistribution();

        // All the values below are cross checked with text at pp 54-55

        assertEquals(0.004608, f.getValue(
                vars(D, I, G, S, L),
                new int[]{0, 1, 1, 1, 0}), 1e-5);

        VarSet marginalizedL = f.getScope().removeVars(L);
        VarSet marginalizedI = f.getScope().removeVars(I);
        VarSet marginalizedD = f.getScope().removeVars(D);

        // Compute P(L)
        Factor lDistribution = f.marginalize(marginalizedL);
        assertEquals(
                "Factor({L(2)}, [0.4976639999999999, 0.5023359999999999])",
                lDistribution.toString());

        // Compute P(L|i0)
        assertEquals(
                "Factor({L(2)}, [0.6114, 0.3886])",
                f.observeEvidence(vars(I), new int[]{0}).marginalize(marginalizedL).normalize().toString());

        // Compute P(L|i0, d0)
        assertEquals(
                "Factor({L(2)}, [0.48699999999999993, 0.5130000000000001])",
                f.observeEvidence(vars(I, D), new int[]{0, 0}).marginalize(marginalizedL).normalize().toString());

        // Compute P(I)
        assertEquals(
                "Factor({I(2)}, [0.7000000000000001, 0.3])",
                f.marginalize(marginalizedI).normalize().toString());

        // Compute P(I|g3), P(D|g3)
        assertEquals(
                "Factor({I(2)}, [0.9210526315789473, 0.07894736842105267])",
                f.observeEvidence(vars(G), new int[]{2}).marginalize(marginalizedI).normalize().toString());
        assertEquals(
                "Factor({D(2)}, [0.37070938215102983, 0.6292906178489702])",
                f.observeEvidence(vars(G), new int[]{2}).marginalize(marginalizedD).normalize().toString());

        // Compute P(I|l0), P(I|g3, l0), P(I|g3, s1)
        assertEquals(
                "Factor({I(2)}, [0.8599778163580247, 0.14002218364197533])",
                f.observeEvidence(vars(L), new int[]{0}).marginalize(marginalizedI).normalize().toString());
        assertEquals(
                "Factor({I(2)}, [0.9210526315789473, 0.07894736842105265])",
                f.observeEvidence(vars(G, L), new int[]{2, 0}).marginalize(marginalizedI).normalize().toString());
        assertEquals(
                "Factor({I(2)}, [0.4216867469879517, 0.5783132530120483])",
                f.observeEvidence(vars(G, S), new int[]{2, 1}).marginalize(marginalizedI).normalize().toString());
    }
}
