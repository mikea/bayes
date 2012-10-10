package com.mikea.bayes.examples;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Factor;
import org.junit.Test;

import static com.mikea.bayes.BNFixture.D;
import static com.mikea.bayes.BNFixture.G;
import static com.mikea.bayes.BNFixture.I;
import static com.mikea.bayes.BNFixture.L;
import static com.mikea.bayes.BNFixture.S;
import static com.mikea.bayes.BNFixture.buildStudentsNetwork;
import static com.mikea.bayes.Var.vars;
import static com.mikea.bayes.VarAssignment.at;
import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;

/**
 * Applying library to Students example from Koller.
 *
 * @author mike.aizatsky@gmail.com
 */
public class StudentsTest {
    @Test
    public void testValidate() throws Exception {
        BayesianNetwork n = buildStudentsNetwork();
        n.validate();
    }

    @Test
    public void testComputeProbability() throws Exception {
        BayesianNetwork n = buildStudentsNetwork();
        assertEquals(0.004608, n.computeProbability(at(D, 0).at(I, 1).at(G, 1).at(S, 1).at(L, 0)), 1e-5);
    }

    @Test
    public void testJointDistribution() throws Exception {
        BayesianNetwork n = buildStudentsNetwork();
        Factor f = n.computeJointDistribution();

        // All the values below are cross checked with text at pp 54-55

        assertEquals(0.004608, f.getValue(
                vars(D, I, G, S, L),
                new int[]{0, 1, 1, 1, 0}), 1e-5);

        // TODO: use boolean comparisons

        // Compute P(L)
        assertEquals(
                "Factor({L(2)}, [0.497664, 0.5023360000000001])",
                n.query(L).toString());

        // Compute P(L|i0)
        assertEquals(
                "Factor({L(2)}, [0.6113999999999999, 0.38860000000000006])",
                n.query(newVarSet(L), vars(I), ints(0)).toString());

        // Compute P(L|i0, d0)
        assertEquals(
                "Factor({L(2)}, [0.48699999999999993, 0.513])",
                n.query(newVarSet(L), vars(I, D), ints(0, 0)).toString());

        // Compute P(I)
        assertEquals(
                "Factor({I(2)}, [0.7, 0.3])",
                n.query(I).toString());

        // Compute P(I|g3), P(D|g3)
        assertEquals(
                "Factor({I(2)}, [0.9210526315789473, 0.07894736842105264])",
                n.query(newVarSet(I), vars(G), new int[]{2}).toString());
        assertEquals(
                "Factor({D(2)}, [0.3707093821510298, 0.6292906178489702])",
                n.query(newVarSet(D), vars(G), new int[]{2}).toString());

        // Compute P(I|l0), P(I|g3, l0), P(I|g3, s1)
        assertEquals(
                "Factor({I(2)}, [0.8599778163580247, 0.14002218364197533])",
                n.query(newVarSet(I), vars(L), new int[]{0}).toString());
        assertEquals(
                "Factor({I(2)}, [0.9210526315789473, 0.07894736842105264])",
                n.query(newVarSet(I), vars(G, L), new int[]{2, 0}).toString());
        assertEquals(
                "Factor({I(2)}, [0.4216867469879517, 0.5783132530120483])",
                n.query(newVarSet(I), vars(G, S), new int[]{2, 1}).toString());
    }

    private static int[] ints(int...values) {
        return values;
    }
}
