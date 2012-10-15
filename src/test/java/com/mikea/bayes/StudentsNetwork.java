package com.mikea.bayes;

import static com.mikea.bayes.Factor.newFactor;
import static com.mikea.bayes.Var.vars;
import static com.mikea.bayes.VarAssignment.at;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class StudentsNetwork {
    public static final ProbabilitySpace space = new ProbabilitySpace("StudentsNetwork");
    public static final Var D = space.newVar("D", 2);
    public static final Var I = space.newVar("I", 2);
    public static final Var G = space.newVar("G", 3);
    public static final Var S = space.newVar("S", 2);
    public static final Var L = space.newVar("L", 2);

    public static BayesianNetwork buildStudentsNetwork() {
        Factor factorD = newFactor(vars(D), new double[]{0.6, 0.4});
        Factor factorI = newFactor(vars(I), new double[]{0.7, 0.3});
        Factor factorG = newFactor(vars(G, I, D), new double[]{
                0.3, 0.05, 0.9, 0.5, 0.4, 0.25, 0.08, 0.3, 0.3, 0.7, 0.02, 0.2});
        Factor factorS = newFactor(vars(S, I), new double[]{
                0.95, 0.2, 0.05, 0.8});
        Factor factorL = newFactor(vars(L, G), new double[]{
                0.1, 0.4, 0.99, 0.9, 0.6, 0.01});

        // check some factor values
        assertEquals(0.3,
                factorG.getValue(at(G, 0).at(D, 0).at(I, 0)),
                1e-5);
        assertEquals(0.4,
                factorG.getValue(at(G, 1).at(D, 0).at(I, 0)),
                1e-5);
        assertEquals(0.3,
                factorG.getValue(at(G, 2).at(D, 0).at(I, 0)),
                1e-5);

        assertEquals(0.95,
                factorS.getValue(at(S, 0).at(I, 0)), 1e-5);
        assertEquals(0.05,
                factorS.getValue(at(S, 1).at(I, 0)), 1e-5);

        return BayesianNetwork
                .withVariables(D, I, G, S, L)
                .edge(D, G)
                .edge(I, G)
                .edge(I, S)
                .edge(G, L)
                .factor(D, factorD)
                .factor(I, factorI)
                .factor(G, factorG)
                .factor(S, factorS)
                .factor(L, factorL)
                .build();
    }
}
