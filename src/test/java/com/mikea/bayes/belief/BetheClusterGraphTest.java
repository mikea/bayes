package com.mikea.bayes.belief;

import com.google.common.collect.ImmutableList;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.Var;
import org.junit.Test;

import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class BetheClusterGraphTest {
    @Test
    public void testSmoke() throws Exception {
        ProbabilitySpace space = new ProbabilitySpace();

        Var a = space.newVar("a", 2);
        Var b = space.newVar("b", 2);
        Var c = space.newVar("c", 2);

        ClusterGraph graph = BetheClusterGraph.buildBetheClusterGraph(ImmutableList.of(
                newVarSet(a, b), newVarSet(b, c), newVarSet(a, c)));

        assertEquals(
                "DataGraphImpl{isDirected=false, [{a, b}, {a, c}, {a}, {b, c}, {b}, {c}], [\n" +
                        "    {a, b}<->{a}:{a}\n" +
                        "    {a, b}<->{b}:{b}\n" +
                        "    {a, c}<->{a}:{a}\n" +
                        "    {a, c}<->{c}:{c}\n" +
                        "    {b, c}<->{b}:{b}\n" +
                        "    {b, c}<->{c}:{c}\n" +
                        "]}",
                graph.toString());
    }

    @Test
    public void testSingleVarVarSet() throws Exception {
        ProbabilitySpace space = new ProbabilitySpace();

        Var a = space.newVar("a", 2);
        Var b = space.newVar("b", 2);
        Var c = space.newVar("c", 2);

        ClusterGraph graph = BetheClusterGraph.buildBetheClusterGraph(ImmutableList.of(
                newVarSet(a, b), newVarSet(b, c), newVarSet(c)));

        assertEquals(
                "DataGraphImpl{isDirected=false, [{a, b}, {a}, {b, c}, {b}, {c}], [\n" +
                        "    {a, b}<->{a}:{a}\n" +
                        "    {a, b}<->{b}:{b}\n" +
                        "    {b, c}<->{b}:{b}\n" +
                        "    {b, c}<->{c}:{c}\n" +
                        "]}",
                graph.toString());
    }
}
