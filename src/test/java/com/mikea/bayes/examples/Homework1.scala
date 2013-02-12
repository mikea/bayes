package com.mikea.bayes.examples

import org.junit.Test
import org.junit.Assert.assertEquals
import com.mikea.bayes._

/**
 * Programming assignment 1 for PGM.
 *
 * @author mike.aizatsky@gmail.com
 */
class Homework1 {
  @Test def testInterCasualReasoning() {
    val space: ProbabilitySpace = new ProbabilitySpace
    val P: Var = space.newVar("P", 2)
    val A: Var = space.newVar("A", 2)
    val T: Var = space.newVar("T", 2)
    val fp: Factor = Factor.newFactor(VarSet(P), Array[Double](0.99, 0.01))
    val fa: Factor = Factor.newFactor(VarSet(A), Array[Double](0.9, 0.1))
    val ft: Factor = Factor.newFactor(VarSet(T, P, A), Array[Double](0.9, 0.5, 0.4, 0.1, 0.1, 0.5, 0.6, 0.9))
    assertEquals(0.1, ft.getValue(Array(A, P, T), Array(0, 0, 1)), 1e-6)
    assertEquals(0.6, ft.getValue(Array(A, P, T), Array(0, 1, 1)), 1e-6)
    assertEquals(0.5, ft.getValue(Array(A, P, T), Array(1, 0, 1)), 1e-6)
    assertEquals(0.9, ft.getValue(Array(A, P, T), Array(1, 1, 1)), 1e-6)
    val network: BayesianNetwork = BayesianNetwork.withVariables(P, A, T).edge(P, T).edge(A, T).factor(P, fp).factor(A, fa).factor(T, ft).build()
    assertEquals("Factor({A}, [0.6521739130434783, 0.3478260869565217])", network.query(VarSet(A), new VarAssignment(List(T), Array[Int](1))).toString)
    assertEquals("Factor({A}, [0.8571428571428572, 0.14285714285714288])", network.query(VarSet(A), new VarAssignment(List(T, P), Array[Int](1, 1))).toString)
  }

  @Test def testDSeparation() {
    val space: ProbabilitySpace = new ProbabilitySpace
    val A: Var = space.newVar("A", 2)
    val B: Var = space.newVar("B", 2)
    val C: Var = space.newVar("C", 2)
    val D: Var = space.newVar("D", 2)
    val E: Var = space.newVar("E", 2)
    val network: BayesianNetwork = BayesianNetwork.withVariables(A, B, C, D, E).edge(A, C).edge(A, E).edge(B, C).edge(B, D).edge(C, E).build()
    assertEquals("List((A,B), (A,D))", DSeparation.findAllDSeparatedPairs(network, VarSet()).toString())
    assertEquals("List()", DSeparation.findAllDSeparatedPairs(network, VarSet(E)).toString())
  }
}