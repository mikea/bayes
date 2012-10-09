package com.mikea.bayes.examples;

import com.mikea.bayes.*;
import org.junit.Test;

import static com.mikea.bayes.Factor.newFactor;
import static com.mikea.bayes.Var.newVar;
import static com.mikea.bayes.Var.vars;
import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;

/**
 * Programming assignment 1 for PGM.
 *
 * @author mike.aizatsky@gmail.com
 */
public class Homework1 {

    @Test
    public void testInterCasualReasoning() throws Exception {
        // P, A, T
        Var P = newVar("P",  2);
        Var A = newVar("A",  2);
        Var T = newVar("T",  2);

        Factor fp = newFactor(vars(P), new double[]{0.99, 0.01});
        Factor fa = newFactor(vars(A), new double[]{0.9, 0.1});
        Factor ft = newFactor(vars(A, P, T),
                new double[]{
                        // a0p0, a1p0, a0p1, a1p1
                           0.9,  0.5,  0.4,  0.1,  // t0
                           0.1,  0.5,  0.6,  0.9,  // t1
                });


        assertEquals(0.1, ft.getValue(vars(A, P, T), new int[]{0, 0, 1}), 1e-6);
        assertEquals(0.6, ft.getValue(vars(A, P, T), new int[]{0, 1, 1}), 1e-6);
        assertEquals(0.5, ft.getValue(vars(A, P, T), new int[]{1, 0, 1}), 1e-6);
        assertEquals(0.9, ft.getValue(vars(A, P, T), new int[]{1, 1, 1}), 1e-6);

        BayesianNetwork network = BayesianNetwork
                .withVariables(P, A, T)
                .edge(P, T)
                .edge(A, T)
                .factor(P, fp)
                .factor(A, fa)
                .factor(T, ft)
                .build();
        Factor jointDistribution = network.computeJointDistribution();

        VarSet marginalizedA = jointDistribution.getScope().removeVars(A);

        assertEquals("Factor({A(2)}, [0.6521739130434783, 0.3478260869565217])",
                jointDistribution.observeEvidence(vars(T), new int[]{1}).marginalize(marginalizedA).normalize().toString());
        assertEquals("Factor({A(2)}, [0.8571428571428572, 0.14285714285714288])",
                jointDistribution.observeEvidence(vars(T, P), new int[]{1, 1}).marginalize(marginalizedA).normalize().toString());
    }

    @Test
    public void testDSeparation() throws Exception {
        // A, B, C, D, E
        Var A = newVar("A",  2);
        Var B = newVar("B",  2);
        Var C = newVar("C",  2);
        Var D = newVar("D",  2);
        Var E = newVar("E",  2);

        BayesianNetwork network = BayesianNetwork
                .withVariables(A, B, C, D, E)
                .edge(A, C)
                .edge(A, E)
                .edge(B, C)
                .edge(B, D)
                .edge(C, E)
                .build();

        assertEquals("[(A, B), (A, D)]",
                DSeparation.findAllDSeparatedPairs(network, newVarSet()).toString());
        assertEquals("[]",
                DSeparation.findAllDSeparatedPairs(network, newVarSet(E)).toString());
    }

}
