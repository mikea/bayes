package com.mikea.bayes.belief;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.StudentsNetwork;
import org.junit.Test;

/**
 * @author mike.aizatsky@gmail.com
 */
public class ClusterGraphsTest {
    @Test
    public void testValidate() throws Exception {
        BayesianNetwork network = StudentsNetwork.buildStudentsNetwork();
        ClusterGraph graph = CliqueTree.buildCliqueTree(network);

        ClusterGraphs.validate(graph, network.getFactorList());
    }
}
