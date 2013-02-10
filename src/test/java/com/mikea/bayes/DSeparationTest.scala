package com.mikea.bayes

import org.junit.Test

/**
 * @author mike.aizatsky@gmail.com
 */
class DSeparationTest {
  @Test def testGraph1 {
    ???
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val W: Var = space.newVar("W", 2)
    val X: Var = space.newVar("X", 2)
    val Y: Var = space.newVar("Y", 2)
    val Z: Var = space.newVar("Z", 2)
    val network: BayesianNetwork = BayesianNetwork.withVariables(W, X, Y, Z).edge(W, Y).edge(Z, Y).edge(Y, X).edge(Z, X).build
    Assert.assertEquals("{Y}", DSeparation.findDSeparation(network, X, newVarSet(Y)).toString)
    Assert.assertEquals("{}", DSeparation.findDSeparation(network, X, newVarSet).toString)
*/
  }

  @Test def testGraph2 {
    ???
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val A: Var = space.newVar("A", 2)
    val B: Var = space.newVar("B", 2)
    val C: Var = space.newVar("C", 2)
    val D: Var = space.newVar("D", 2)
    val E: Var = space.newVar("E", 2)
    val network: BayesianNetwork = BayesianNetwork.withVariables(A, B, C, D, E).edge(A, C).edge(A, E).edge(B, C).edge(B, D).edge(C, E).build
    Assert.assertEquals("[(A, B), (A, D)]", DSeparation.findAllDSeparatedPairs(network, newVarSet).toString)
    Assert.assertEquals("[]", DSeparation.findAllDSeparatedPairs(network, newVarSet(E)).toString)
*/
  }
}