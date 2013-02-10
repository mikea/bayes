package com.mikea.bayes.belief

import com.mikea.bayes.BayesianNetwork
import com.mikea.bayes.data.StudentsNetwork
import org.junit.Test

/**
 * @author mike.aizatsky@gmail.com
 */
class ClusterGraphsTest {
  @Test def testValidate {
    val network: BayesianNetwork = StudentsNetwork.buildStudentsNetwork
    val graph: ClusterGraph = CliqueTree.buildCliqueTree(network)
    ClusterGraphs.validate(graph, network.getFactorList)
  }
}