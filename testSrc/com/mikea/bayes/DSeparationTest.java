package com.mikea.bayes;

import org.junit.Assert;
import org.junit.Test;

import static com.mikea.bayes.Var.newVar;
import static com.mikea.bayes.VarSet.newVarSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class DSeparationTest {
    @Test
    public void testGraph1() throws Exception {
        // Example 3.6 in Koller
        Var W = newVar("W",  2);
        Var X = newVar("X",  2);
        Var Y = newVar("Y",  2);
        Var Z = newVar("Z",  2);

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
        Assert.assertEquals("[(0, 1), (0, 3)]", DSeparation.findAllDSeparatedPairs(network, newVarSet()).toString());
        Assert.assertEquals("[(0, 4), (1, 4), (2, 4), (3, 4)]",
                DSeparation.findAllDSeparatedPairs(network, newVarSet(E)).toString());
    }
}
