package com.mikea.bayes;

import org.gga.graph.MutableGraph;
import org.gga.graph.impl.SparseGraphImpl;
import org.junit.Assert;
import org.junit.Test;

import static com.mikea.bayes.VarSet.newVarSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class DSeparationTest {
    @Test
    public void testGraph1() throws Exception {
        // Example 3.6 in Koller
        // W, X, Y, Z
        ProbabilitySpace space = new ProbabilitySpace(4, new int[] {2, 2, 2, 2});
        MutableGraph g = new SparseGraphImpl(4, true);
        final int W = 0;
        final int X = 1;
        final int Y = 2;
        final int Z = 3;

        g.insert(W, Y);
        g.insert(Z, Y);
        g.insert(Y, X);
        g.insert(Z, X);

        BayesianNetwork network = new BayesianNetwork(space, g, null);

        Assert.assertEquals("{2}", DSeparation.findDSeparation(network, X, newVarSet(space, Y)).toString());
        Assert.assertEquals("{}", DSeparation.findDSeparation(network, X, newVarSet(space)).toString());
    }

    @Test
    public void testGraph2() throws Exception {
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
        Assert.assertEquals("[(0, 1), (0, 3)]", DSeparation.findAllDSeparatedPairs(network, newVarSet(space)).toString());
        Assert.assertEquals("[(0, 4), (1, 4), (2, 4), (3, 4)]",
                DSeparation.findAllDSeparatedPairs(network, newVarSet(space, E)).toString());
    }
}
