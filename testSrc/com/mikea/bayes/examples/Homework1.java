package com.mikea.bayes.examples;

import com.mikea.bayes.*;
import org.gga.graph.MutableGraph;
import org.gga.graph.impl.SparseGraphImpl;
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
        final int P = 0;
        final int A = 1;
        final int T = 2;
        Var varP = newVar("P",  2);
        Var varA = newVar("A",  2);
        Var varT = newVar("T",  2);

        MutableGraph g = new SparseGraphImpl(3, true);

        g.insert(P, T);
        g.insert(A, T);

        Factor fp = newFactor(vars(varP), new double[]{0.99, 0.01});
        Factor fa = newFactor(vars(varA), new double[]{0.9, 0.1});
        Factor ft = newFactor(vars(varA, varP, varT),
                new double[]{
                        // a0p0, a1p0, a0p1, a1p1
                           0.9,  0.5,  0.4,  0.1,  // t0
                           0.1,  0.5,  0.6,  0.9,  // t1
                });


        assertEquals(0.1, ft.getValue(vars(varA, varP, varT), new int[]{0, 0, 1}), 1e-6);
        assertEquals(0.6, ft.getValue(vars(varA, varP, varT), new int[]{0, 1, 1}), 1e-6);
        assertEquals(0.5, ft.getValue(vars(varA, varP, varT), new int[]{1, 0, 1}), 1e-6);
        assertEquals(0.9, ft.getValue(vars(varA, varP, varT), new int[]{1, 1, 1}), 1e-6);

        BayesianNetwork network = new BayesianNetwork(g, vars(varP, varA, varT), new Factor[]{fp, fa, ft});
        Factor jointDistribution = network.computeJointDistribution();

        VarSet marginalizedA = jointDistribution.getVarSet().removeVars(varA);

        assertEquals("Factor({A(2)}, [0.6521739130434783, 0.3478260869565217])",
                jointDistribution.observeEvidence(vars(varT), new int[]{1}).marginalize(marginalizedA).normalize().toString());
        assertEquals("Factor({A(2)}, [0.8571428571428572, 0.14285714285714288])",
                jointDistribution.observeEvidence(vars(varT, varP), new int[]{1, 1}).marginalize(marginalizedA).normalize().toString());
    }

    @Test
    public void testDSeparation() throws Exception {
        // A, B, C, D, E
        MutableGraph g = new SparseGraphImpl(5, true);
        final int A = 0;
        final int B = 1;
        final int C = 2;
        final int D = 3;
        final int E = 4;
        Var varA = newVar("A",  2);
        Var varB = newVar("B",  2);
        Var varC = newVar("C",  2);
        Var varD = newVar("D",  2);
        Var varE = newVar("E",  2);

        g.insert(A, C);
        g.insert(A, E);
        g.insert(B, C);
        g.insert(B, D);
        g.insert(C, E);

        BayesianNetwork network = new BayesianNetwork(g, vars(varA, varB, varC, varD, varE), null);
        assertEquals("[(0, 1), (0, 3)]",
                DSeparation.findAllDSeparatedPairs(network, newVarSet()).toString());
        assertEquals("[(0, 4), (1, 4), (2, 4), (3, 4)]",
                DSeparation.findAllDSeparatedPairs(network, newVarSet(varE)).toString());
    }

}
