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
        // Example 3.6 in Koller
        Var W = new Var("W", 2);
        Var X = new Var("X", 2);
        Var Y = new Var("Y", 2);
        Var Z = new Var("Z", 2);

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
        // A, B, C, D, E
        Var A = new Var("A", 2);
        Var B = new Var("B", 2);
        Var C = new Var("C", 2);
        Var D = new Var("D", 2);
        Var E = new Var("E", 2);

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
