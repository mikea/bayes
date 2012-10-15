package com.mikea.bayes;

import org.junit.Assert;
import org.junit.Test;

import static com.mikea.bayes.VarSet.newVarSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class DSeparationTest {
    @Test
    public void testGraph1() throws Exception {
        ProbabilitySpace space = new ProbabilitySpace();

        // Example 3.6 in Koller
        Var W = space.newVar("W", 2);
        Var X = space.newVar("X", 2);
        Var Y = space.newVar("Y", 2);
        Var Z = space.newVar("Z", 2);

        BayesianNetwork network = BayesianNetwork
                .withVariables(W, X, Y, Z)
                .edge(W, Y)
                .edge(Z, Y)
                .edge(Y, X)
                .edge(Z, X)
                .build();

        Assert.assertEquals("{Y(2)}", DSeparation.findDSeparation(network, X, newVarSet(Y)).toString());
        Assert.assertEquals("{}", DSeparation.findDSeparation(network, X, newVarSet()).toString());
    }

    @Test
    public void testGraph2() throws Exception {
        ProbabilitySpace space = new ProbabilitySpace();

        // A, B, C, D, E
        Var A = space.newVar("A", 2);
        Var B = space.newVar("B", 2);
        Var C = space.newVar("C", 2);
        Var D = space.newVar("D", 2);
        Var E = space.newVar("E", 2);

        BayesianNetwork network = BayesianNetwork
                .withVariables(A, B, C, D, E)
                .edge(A, C)
                .edge(A, E)
                .edge(B, C)
                .edge(B, D)
                .edge(C, E)
                .build();
        Assert.assertEquals("[(A, B), (A, D)]", DSeparation.findAllDSeparatedPairs(network, newVarSet()).toString());
        Assert.assertEquals("[]",
                DSeparation.findAllDSeparatedPairs(network, newVarSet(E)).toString());
    }
}
