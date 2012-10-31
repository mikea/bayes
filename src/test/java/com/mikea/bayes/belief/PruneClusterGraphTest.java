package com.mikea.bayes.belief;

import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarSet;
import org.gga.graph.impl.DataGraphImpl;
import org.junit.Test;

import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class PruneClusterGraphTest {
    @Test
    public void testDoesntKillGraph() throws Exception {
        ProbabilitySpace space = new ProbabilitySpace();

        Var a = space.newVar("a", 2);
        Var b = space.newVar("b", 2);
        Var c = space.newVar("c", 2);

        VarSet abSet = newVarSet(a, b);
        VarSet acSet = newVarSet(a, c);
        VarSet aSet = newVarSet(a);

        DataGraphImpl.Builder<VarSet, VarSet> builder = new DataGraphImpl.Builder<VarSet, VarSet>(VarSet.class, VarSet.class, false);
        builder
                .addNode(abSet)
                .addNode(acSet)
                .addNode(aSet)
                .addEdge(aSet, abSet, aSet)
                .addEdge(aSet, acSet, aSet);

        ClusterGraph prunedGraph = PruneClusterGraph.prune(new ClusterGraphImpl(space, builder.build()));
        assertEquals(
                "DataGraphImpl{isDirected=false, [{a, b}, {a, c}], [\n" +
                        "    {a, b}<->{a, c}:{a}\n" +
                        "]}",
                prunedGraph.toString()
        );
    }
}
