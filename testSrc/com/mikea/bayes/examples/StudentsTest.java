package com.mikea.bayes.examples;

import com.mikea.bayes.*;
import org.gga.graph.MutableGraph;
import org.gga.graph.impl.SparseGraphImpl;
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
    public static final int D = 0;
    public static final int I = 1;
    public static final int G = 2;
    public static final int S = 3;
    public static final int L = 4;

    private static final Var varD = newVar("D", 2);
    private static final Var varI = newVar("I", 2);
    private static final Var varG = newVar("G", 3);
    private static final Var varS = newVar("S", 2);
    private static final Var varL = newVar("L", 2);

    private static BayesianNetwork buildNetwork() {
        MutableGraph g = new SparseGraphImpl(5, true);

        g.insert(D, G);
        g.insert(I, G);
        g.insert(I, S);
        g.insert(G, L);

        Factor factor0 = newFactor(vars(varD), new double[]{0.6, 0.4});
        Factor factor1 = newFactor(vars(varI), new double[]{0.7, 0.3});
        Factor factor2 = newFactor(vars(varG, varD, varI), new double[]{0.3, 0.4, 0.3, 0.05, 0.25, 0.7, 0.9, 0.08, 0.02, 0.5, 0.3, 0.2});
        Factor factor3 = newFactor(vars(varS, varI), new double[]{0.95, 0.05, 0.2, 0.8});
        Factor factor4 = newFactor(vars(varL, varG), new double[]{0.1, 0.9, 0.4, 0.6, 0.99, 0.01});

        // check some factor values
        assertEquals(0.3,
                factor2.getValue(at(varG, 0).at(varD, 0).at(varI, 0)),
                1e-5);
        assertEquals(0.4,
                factor2.getValue(at(varG, 1).at(varD, 0).at(varI, 0)),
                1e-5);
        assertEquals(0.3,
                factor2.getValue(at(varG, 2).at(varD, 0).at(varI, 0)),
                1e-5);

        assertEquals(0.95,
                factor3.getValue(at(varS, 0).at(varI, 0)), 1e-5);
        assertEquals(0.05,
                factor3.getValue(at(varS, 1).at(varI, 0)), 1e-5);

        return new BayesianNetwork(g, vars(varD, varI, varG, varS, varL), new Factor[]{factor0, factor1, factor2, factor3, factor4});
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

        assertEquals(0.004608, f.getValue(
                vars(varD, varI, varG, varS, varL),
                new int[]{0, 1, 1, 1, 0}), 1e-5);

        VarSet marginalizedL = f.getVarSet().removeVars(varL);
        VarSet marginalizedI = f.getVarSet().removeVars(varI);
        VarSet marginalizedD = f.getVarSet().removeVars(varD);

        // Compute P(L)
        Factor lDistribution = f.marginalize(marginalizedL);
        assertEquals(
                "Factor({L(2)}, [0.4976639999999999, 0.5023359999999999])",
                lDistribution.toString());

        // Compute P(L|i0)
        assertEquals(
                "Factor({L(2)}, [0.6114, 0.3886])",
                f.observeEvidence(vars(varI), new int[]{0}).marginalize(marginalizedL).normalize().toString());

        // Compute P(L|i0, d0)
        assertEquals(
                "Factor({L(2)}, [0.48699999999999993, 0.5130000000000001])",
                f.observeEvidence(vars(varI, varD), new int[]{0, 0}).marginalize(marginalizedL).normalize().toString());

        // Compute P(I)
        assertEquals(
                "Factor({I(2)}, [0.7000000000000001, 0.3])",
                f.marginalize(marginalizedI).normalize().toString());

        // Compute P(I|g3), P(D|g3)
        assertEquals(
                "Factor({I(2)}, [0.9210526315789473, 0.07894736842105267])",
                f.observeEvidence(vars(varG), new int[]{2}).marginalize(marginalizedI).normalize().toString());
        assertEquals(
                "Factor({D(2)}, [0.37070938215102983, 0.6292906178489702])",
                f.observeEvidence(vars(varG), new int[]{2}).marginalize(marginalizedD).normalize().toString());

        // Compute P(I|l0), P(I|g3, l0), P(I|g3, s1)
        assertEquals(
                "Factor({I(2)}, [0.8599778163580247, 0.14002218364197533])",
                f.observeEvidence(vars(varL), new int[]{0}).marginalize(marginalizedI).normalize().toString());
        assertEquals(
                "Factor({I(2)}, [0.9210526315789473, 0.07894736842105265])",
                f.observeEvidence(vars(varG, varL), new int[]{2, 0}).marginalize(marginalizedI).normalize().toString());
        assertEquals(
                "Factor({I(2)}, [0.4216867469879517, 0.5783132530120483])",
                f.observeEvidence(vars(varG, varS), new int[]{2, 1}).marginalize(marginalizedI).normalize().toString());
    }
}
