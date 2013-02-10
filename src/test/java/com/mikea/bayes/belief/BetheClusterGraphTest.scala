package com.mikea.bayes.belief

import org.junit.Test

/**
 * @author mike.aizatsky@gmail.com
 */
class BetheClusterGraphTest {
  @Test def testSmoke {
    ???
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val a: Var = space.newVar("a", 2)
    val b: Var = space.newVar("b", 2)
    val c: Var = space.newVar("c", 2)
    val graph: ClusterGraph = BetheClusterGraph.buildBetheClusterGraph(ImmutableList.of(newVarSet(a, b), newVarSet(b, c), newVarSet(a, c)))
    assertEquals("DataGraphImpl{isDirected=false, [{a, b}, {a, c}, {a}, {b, c}, {b}, {c}], [\n" + "    {a, b}<->{a}:{a}\n" + "    {a, b}<->{b}:{b}\n" + "    {a, c}<->{a}:{a}\n" + "    {a, c}<->{c}:{c}\n" + "    {b, c}<->{b}:{b}\n" + "    {b, c}<->{c}:{c}\n" + "]}", graph.toString)
*/
  }

  @Test def testSingleVarVarSet {
    ???
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val a: Var = space.newVar("a", 2)
    val b: Var = space.newVar("b", 2)
    val c: Var = space.newVar("c", 2)
    val graph: ClusterGraph = BetheClusterGraph.buildBetheClusterGraph(ImmutableList.of(newVarSet(a, b), newVarSet(b, c), newVarSet(c)))
    assertEquals("DataGraphImpl{isDirected=false, [{a, b}, {a}, {b, c}, {b}, {c}], [\n" + "    {a, b}<->{a}:{a}\n" + "    {a, b}<->{b}:{b}\n" + "    {b, c}<->{b}:{b}\n" + "    {b, c}<->{c}:{c}\n" + "]}", graph.toString)
*/
  }
}