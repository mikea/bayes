package com.mikea.bayes

import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * @author mike.aizatsky@gmail.com
 */
class IMapTest {
  @Test def testImap1 {
    val space: ProbabilitySpace = new ProbabilitySpace
    val A: Var = space.newVar("A", 2)
    val B: Var = space.newVar("B", 2)
    val C: Var = space.newVar("C", 2)
    val D: Var = space.newVar("D", 2)
    val E: Var = space.newVar("E", 2)
    val network: BayesianNetwork = BayesianNetwork.withVariables(A, B, C, D, E).edge(A, C).edge(A, E).edge(B, C).edge(B, D).edge(C, E).build
    assertEquals("[(A ⊥ B | {D}), (A ⊥ B | {}), (A ⊥ D | {B, C, E}), (A ⊥ D | {B, C}), (A ⊥ D | {B, E}), (A ⊥ D | {B}), (A ⊥ D | {}), (B ⊥ E | {A, C, D}), (B ⊥ E | {A, C}), (C ⊥ D | {A, B, E}), (C ⊥ D | {A, B}), (C ⊥ D | {B, E}), (C ⊥ D | {B}), (D ⊥ E | {A, B, C}), (D ⊥ E | {A, B}), (D ⊥ E | {A, C}), (D ⊥ E | {B, C}), (D ⊥ E | {B})]", IMap.computeIMap(network).toString)
  }
}