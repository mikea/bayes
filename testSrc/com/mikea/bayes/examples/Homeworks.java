package com.mikea.bayes.examples;

import com.mikea.bayes.*;
import org.gga.graph.MutableGraph;
import org.gga.graph.impl.SparseGraphImpl;
import org.junit.Assert;
import org.junit.Test;

import static com.mikea.bayes.Factor.newFactor;
import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class Homeworks {

    @Test
    public void testInterCasualReasoning() throws Exception {
        // P, A, T
        ProbabilitySpace space = new ProbabilitySpace(3, new int[] {2, 2, 2});
        final int P = 0;
        final int A = 1;
        final int T = 2;
        MutableGraph g = new SparseGraphImpl(3, true);

        g.insert(P, T);
        g.insert(A, T);

        Factor fp = newFactor(space, new int[]{P}, new double[]{0.99, 0.01});
        Factor fa = newFactor(space, new int[]{A}, new double[]{0.9, 0.1});
        Factor ft = newFactor(space, new int[]{A, P, T},
                new double[]{
                        // a0p0, a1p0, a0p1, a1p1
                           0.9,  0.5,  0.4,  0.1,  // t0
                           0.1,  0.5,  0.6,  0.9,  // t1
                });


        assertEquals(0.1, ft.getValue(new int[]{0, 0, 1}), 1e-6);
        assertEquals(0.5, ft.getValue(new int[]{0, 1, 1}), 1e-6);
        assertEquals(0.6, ft.getValue(new int[]{1, 0, 1}), 1e-6);
        assertEquals(0.9, ft.getValue(new int[]{1, 1, 1}), 1e-6);

        BayesianNetwork network = new BayesianNetwork(space, g, new Factor[]{fp, fa, ft});
        Factor jointDistribution = network.computeJointDistribution();

        VarSet marginalizedA = jointDistribution.getVarSet().removeVars(new int[]{A});

        assertEquals("Factor({1}, [0.6521739130434783, 0.3478260869565217])",
                jointDistribution.observeEvidence(new int[] {T}, new int[]{1}).marginalize(marginalizedA).normalize().toString());
        assertEquals("Factor({1}, [0.8571428571428572, 0.14285714285714288])",
                jointDistribution.observeEvidence(new int[] {T, P}, new int[]{1, 1}).marginalize(marginalizedA).normalize().toString());
    }

    @Test
    public void testDSeparation() throws Exception {
        // A, B, C, D, E
        ProbabilitySpace space = new ProbabilitySpace(5, new int[] {2, 2, 2, 2, 2});
        MutableGraph g = new SparseGraphImpl(5, true);
        final int A = 0;
        final int B = 1;
        final int C = 2;
        final int D = 3;
        final int E = 4;

        g.insert(A, C);
        g.insert(A, E);
        g.insert(B, C);
        g.insert(B, D);
        g.insert(C, E);

        BayesianNetwork network = new BayesianNetwork(space, g, null);
        Assert.assertEquals("[(0, 1), (0, 3)]",
                DSeparation.findAllDSeparatedPairs(network, newVarSet(space)).toString());
        Assert.assertEquals("[(0, 4), (1, 4), (2, 4), (3, 4)]",
                DSeparation.findAllDSeparatedPairs(network, newVarSet(space, E)).toString());
    }

}
