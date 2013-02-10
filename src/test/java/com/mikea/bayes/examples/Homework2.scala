package com.mikea.bayes.examples

import org.junit.Test

/**
 * @author mike.aizatsky@gmail.com
 */
class Homework2 {
  @Test def testImap1 {
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val a: Var = space.newVar("A", 2)
    val b: Var = space.newVar("B", 2)
    val c: Var = space.newVar("C", 2)
    val d: Var = space.newVar("D", 2)
    val g: BayesianNetwork = BayesianNetwork.withVariables(a, b, c, d).edge(a, b).edge(b, c).edge(b, d).build
    assertEquals("[" + "(A ⊥ C | {B, D}), " + "(A ⊥ C | {B}), " + "(A ⊥ D | {B, C}), " + "(A ⊥ D | {B}), " + "(C ⊥ D | {A, B}), " + "(C ⊥ D | {B})]", IMap.computeIMap(g).toString)
*/
    ???
  }

  @Test def testImap {
    ???
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val a: Var = space.newVar("A", 2)
    val b: Var = space.newVar("B", 2)
    val c: Var = space.newVar("C", 2)
    val d: Var = space.newVar("D", 2)
    val e: Var = space.newVar("E", 2)
    val g: BayesianNetwork = BayesianNetwork.withVariables(a, b, c, d, e).edge(a, b).edge(a, c).edge(b, d).edge(c, d).edge(d, e).build
    val m: IMap = IMap.computeIMap(g)
    assertEquals("[(A ⊥ D | {B, C, E}), (A ⊥ D | {B, C}), (A ⊥ E | {B, C, D}), (A ⊥ E | {B, C}), (A ⊥ E | {B, D}), (A ⊥ E | {C, D}), (A ⊥ E | {D}), (B ⊥ C | {A}), (B ⊥ E | {A, C, D}), (B ⊥ E | {A, D}), (B ⊥ E | {C, D}), (B ⊥ E | {D}), (C ⊥ E | {A, B, D}), (C ⊥ E | {A, D}), (C ⊥ E | {B, D}), (C ⊥ E | {D})]", m.toString)
    val g1: BayesianNetwork = BayesianNetwork.withVariables(a, b, c, d, e).edge(a, b).edge(b, d).edge(c, d).edge(d, e).build
    val m1: IMap = IMap.computeIMap(g1)
    assertEquals("[(A ⊥ C | {B, D, E}), (A ⊥ C | {B, D}), (A ⊥ C | {B, E}), (A ⊥ C | {B}), (A ⊥ C | {}), (A ⊥ D | {B, C, E}), (A ⊥ D | {B, C}), (A ⊥ D | {B, E}), (A ⊥ D | {B}), (A ⊥ E | {B, C, D}), (A ⊥ E | {B, C}), (A ⊥ E | {B, D}), (A ⊥ E | {B}), (A ⊥ E | {C, D}), (A ⊥ E | {D}), (B ⊥ C | {A}), (B ⊥ C | {}), (B ⊥ E | {A, C, D}), (B ⊥ E | {A, D}), (B ⊥ E | {C, D}), (B ⊥ E | {D}), (C ⊥ E | {A, B, D}), (C ⊥ E | {A, D}), (C ⊥ E | {B, D}), (C ⊥ E | {D})]", m1.toString)
    val g2: BayesianNetwork = BayesianNetwork.withVariables(a, b, c, d, e).edge(e, d).edge(d, b).edge(d, c).edge(b, a).edge(c, a).build
    val m2: IMap = IMap.computeIMap(g2)
    assertEquals("[(A ⊥ D | {B, C, E}), (A ⊥ D | {B, C}), (A ⊥ E | {B, C, D}), (A ⊥ E | {B, C}), (A ⊥ E | {B, D}), (A ⊥ E | {C, D}), (A ⊥ E | {D}), (B ⊥ C | {D, E}), (B ⊥ C | {D}), (B ⊥ E | {A, C, D}), (B ⊥ E | {A, D}), (B ⊥ E | {C, D}), (B ⊥ E | {D}), (C ⊥ E | {A, B, D}), (C ⊥ E | {A, D}), (C ⊥ E | {B, D}), (C ⊥ E | {D})]", m2.toString)
    val g3: BayesianNetwork = BayesianNetwork.withVariables(a, b, c, d, e).edge(e, d).edge(d, b).edge(d, c).edge(d, a).edge(b, a).edge(b, c).edge(c, a).build
    val m3: IMap = IMap.computeIMap(g3)
    assertEquals("[(A ⊥ E | {B, C, D}), (A ⊥ E | {B, D}), (A ⊥ E | {C, D}), (A ⊥ E | {D}), (B ⊥ E | {A, C, D}), (B ⊥ E | {A, D}), (B ⊥ E | {C, D}), (B ⊥ E | {D}), (C ⊥ E | {A, B, D}), (C ⊥ E | {A, D}), (C ⊥ E | {B, D}), (C ⊥ E | {D})]", m3.toString)
    assertFalse(m.contains(m1))
    assertFalse(m.contains(m2))
    assertTrue(m.contains(m3))
*/
  }

  @Test def testIEquivalent {
    ???
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val a: Var = space.newVar("A", 2)
    val b: Var = space.newVar("B", 2)
    val c: Var = space.newVar("C", 2)
    val d: Var = space.newVar("D", 2)
    val e: Var = space.newVar("E", 2)
    val g: BayesianNetwork = BayesianNetwork.withVariables(a, b, c, d, e).edge(a, b).edge(a, c).edge(b, d).edge(c, d).edge(c, e).build
    val g1: BayesianNetwork = BayesianNetwork.withVariables(a, b, c, d, e).edge(a, b).edge(b, d).edge(c, a).edge(c, d).edge(e, c).build
    val g2: BayesianNetwork = BayesianNetwork.withVariables(a, b, c, d, e).edge(a, c).edge(b, a).edge(b, d).edge(c, d).edge(e, c).build
    val g3: BayesianNetwork = BayesianNetwork.withVariables(a, b, c, d, e).edge(b, a).edge(b, d).edge(c, a).edge(c, d).edge(c, e).build
    val g4: BayesianNetwork = BayesianNetwork.withVariables(a, b, c, d, e).edge(a, b).edge(a, c).edge(c, e).edge(d, b).edge(d, c).build
    val m: IMap = IMap.computeIMap(g)
    val m1: IMap = IMap.computeIMap(g1)
    val m2: IMap = IMap.computeIMap(g2)
    val m3: IMap = IMap.computeIMap(g3)
    val m4: IMap = IMap.computeIMap(g4)
    assertEquals(m, m1)
    assertFalse(m == m2)
    assertFalse(m == m3)
    assertFalse(m == m4)
*/
  }
}