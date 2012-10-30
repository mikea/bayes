package com.mikea.bayes.examples;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mikea.bayes.Evidence;
import com.mikea.bayes.Factor;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarSet;
import com.mikea.bayes.belief.BeliefPropagationAlgorithm;
import com.mikea.bayes.belief.ClusterGraphImpl;
import org.gga.graph.impl.DataGraphImpl;
import org.junit.Test;

import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class Quiz7 {
    @Test
    public void testBP() throws Exception {
        ProbabilitySpace space = new ProbabilitySpace();

        Var x1 = space.newVar("x1", 2);
        Var x2 = space.newVar("x2", 2);
        Var x3 = space.newVar("x3", 2);
        Var x4 = space.newVar("x4", 2);
        Var x5 = space.newVar("x5", 2);
        Var x6 = space.newVar("x6", 2);

        VarSet c1 = newVarSet(x1, x4);
        VarSet c2 = newVarSet(x1, x2);
        VarSet c3 = newVarSet(x2, x5);
        VarSet c4 = newVarSet(x2, x3);
        VarSet c5 = newVarSet(x3, x6);
        VarSet c6 = newVarSet(x4, x5);
        VarSet c7 = newVarSet(x5, x6);

        double[] factorCoeffs = { 10, 1, 1, 10};

        Factor f12 = Factor.newFactor(newVarSet(x1, x2), factorCoeffs);
        Factor f14 = Factor.newFactor(newVarSet(x1, x4), factorCoeffs);
        Factor f23 = Factor.newFactor(newVarSet(x2, x3), factorCoeffs);
        Factor f25 = Factor.newFactor(newVarSet(x2, x5), factorCoeffs);
        Factor f36 = Factor.newFactor(newVarSet(x3, x6), factorCoeffs);
        Factor f45 = Factor.newFactor(newVarSet(x4, x5), factorCoeffs);
        Factor f56 = Factor.newFactor(newVarSet(x5, x6), factorCoeffs);

        ImmutableList<Factor> factors = ImmutableList.of(f12, f14, f23, f25, f36, f45, f56);

        DataGraphImpl.Builder<VarSet, VarSet> builder = new DataGraphImpl.Builder<VarSet, VarSet>(VarSet.class, VarSet.class, false);
        builder.addNode(c1);
        builder.addNode(c2);
        builder.addNode(c3);
        builder.addNode(c4);
        builder.addNode(c5);
        builder.addNode(c6);
        builder.addNode(c7);
        builder.addEdge(c1, c2, c1.intersect(c2));
        builder.addEdge(c2, c3, c2.intersect(c3));
        builder.addEdge(c3, c4, c3.intersect(c4));
        builder.addEdge(c4, c5, c4.intersect(c5));
        builder.addEdge(c1, c6, c1.intersect(c6));
        builder.addEdge(c6, c7, c6.intersect(c7));
        builder.addEdge(c7, c5, c7.intersect(c5));

        ClusterGraphImpl clusterGraph = new ClusterGraphImpl(space, builder.build());

        assertEquals(
                "DataGraphImpl{isDirected=false, [{x1, x2}, {x1, x4}, {x2, x3}, {x2, x5}, {x3, x6}, {x4, x5}, {x5, x6}], [\n" +
                        "    {x1, x2}<->{x2, x5}:{x2}\n" +
                        "    {x1, x4}<->{x1, x2}:{x1}\n" +
                        "    {x1, x4}<->{x4, x5}:{x4}\n" +
                        "    {x2, x3}<->{x3, x6}:{x3}\n" +
                        "    {x2, x5}<->{x2, x3}:{x2}\n" +
                        "    {x3, x6}<->{x5, x6}:{x6}\n" +
                        "    {x4, x5}<->{x5, x6}:{x5}\n" +
                        "]}",
                clusterGraph.toString()
        );


        BeliefPropagationAlgorithm.BPResult result = new BeliefPropagationAlgorithm.BPResult(
                Iterables.toArray(factors, Factor.class), clusterGraph, 100);

        double probability = result.getProbability(new Evidence(new Var[]{x4, x5}, new int[]{1, 1}));
        assertEquals(0.4545, probability, 0.01);
    }
}
