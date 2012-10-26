package com.mikea.bayes.belief;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.StudentsNetwork;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class CliqueTreeTest {
    @Test
    public void testStudentsNetwork() throws Exception {
        BayesianNetwork network = StudentsNetwork.buildStudentsNetwork();

        ClusterGraph graph = CliqueTree.buildCliqueTree(network.getVarList(), network.getFactorList());

        assertEquals(
                "DataGraphImpl{isDirected=false, [{D, G, I, L}, {G}, {I, S}], [\n" +
                        "    {D, G, I, L}<->{G}:{G}\n" +
                        "    {I, S}<->{D, G, I, L}:{I}\n" +
                        "]}",
                graph.toString()
        );

    }
}
