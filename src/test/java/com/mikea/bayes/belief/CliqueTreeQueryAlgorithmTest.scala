package com.mikea.bayes.belief

import org.junit.Test

/**
 * @author mike.aizatsky@gmail.com
 */
class CliqueTreeQueryAlgorithmTest {
  @Test def testNoEvidence {
    ???
/*
    val baselineAlgorithm: QueryAlgorithm[_] = new VarElimination(new SumProduct.MinNeighborsStrategy)
    val algorithm: QueryAlgorithm[_] = new CliqueTreeQueryAlgorithm
    val network: BayesianNetwork = StudentsNetwork.buildStudentsNetwork
    val baselineResult: Nothing = baselineAlgorithm.prepare(network)
    val actualResult: Nothing = algorithm.prepare(network)
    val epsilon: Double = 0.001
    import scala.collection.JavaConversions._
    for (`var` <- network.varList) {
      val expected: Factor = baselineResult.query(newVarSet(`var`))
      val actual: Factor = actualResult.query(newVarSet(`var`))
      assertTrue(expected.equals(actual, epsilon))
      val baselineProbability: Double = baselineResult.getProbability(new VarAssignment(Array[Var](`var`), Array[Int](0)))
      val actualProbability: Double = actualResult.getProbability(new VarAssignment(Array[Var](`var`), Array[Int](0)))
      assertEquals(baselineProbability, actualProbability, epsilon)
    }
*/
  }
}