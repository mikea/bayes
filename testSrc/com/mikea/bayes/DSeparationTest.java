package com.mikea.bayes;

import org.gga.graph.MutableGraph;
import org.gga.graph.impl.SparseGraphImpl;
import org.junit.Assert;
import org.junit.Test;

import static com.mikea.bayes.Var.newVar;
import static com.mikea.bayes.Var.vars;
import static com.mikea.bayes.VarSet.newVarSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class DSeparationTest {
    @Test
    public void testGraph1() throws Exception {
        // Example 3.6 in Koller
        // W, X, Y, Z
        MutableGraph g = new SparseGraphImpl(4, true);
        final int W = 0;
        final int X = 1;
        final int Y = 2;
        final int Z = 3;
        Var varW = newVar("W",  2);
        Var varX = newVar("X",  2);
        Var varY = newVar("Y",  2);
        Var varZ = newVar("Z",  2);

        g.insert(W, Y);
        g.insert(Z, Y);
        g.insert(Y, X);
        g.insert(Z, X);


        BayesianNetwork network = new BayesianNetwork(g, vars(varW, varX, varY, varZ), null);

        Assert.assertEquals("{Y(2)}", DSeparation.findDSeparation(network, X, newVarSet(varY)).toString());
        Assert.assertEquals("{}", DSeparation.findDSeparation(network, X, newVarSet()).toString());
    }

    @Test
    public void testGraph2() throws Exception {
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
        Assert.assertEquals("[(0, 1), (0, 3)]", DSeparation.findAllDSeparatedPairs(network, newVarSet()).toString());
        Assert.assertEquals("[(0, 4), (1, 4), (2, 4), (3, 4)]",
                DSeparation.findAllDSeparatedPairs(network, newVarSet(varE)).toString());
    }
}
