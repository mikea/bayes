package com.mikea.bayes.examples

import org.junit.Test

/**
 * @author mike.aizatsky@gmail.com
 */
class Quiz7 {
  @Test def testBP {
    ???
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val x1: Var = space.newVar("x1", 2)
    val x2: Var = space.newVar("x2", 2)
    val x3: Var = space.newVar("x3", 2)
    val x4: Var = space.newVar("x4", 2)
    val x5: Var = space.newVar("x5", 2)
    val x6: Var = space.newVar("x6", 2)
    val c1: VarSet = newVarSet(x1, x4)
    val c2: VarSet = newVarSet(x1, x2)
    val c3: VarSet = newVarSet(x2, x5)
    val c4: VarSet = newVarSet(x2, x3)
    val c5: VarSet = newVarSet(x3, x6)
    val c6: VarSet = newVarSet(x4, x5)
    val c7: VarSet = newVarSet(x5, x6)
    val factorCoeffs: Array[Double] = Array(10, 1, 1, 10)
    val f12: Factor = Factor.newFactor(newVarSet(x1, x2), factorCoeffs)
    val f14: Factor = Factor.newFactor(newVarSet(x1, x4), factorCoeffs)
    val f23: Factor = Factor.newFactor(newVarSet(x2, x3), factorCoeffs)
    val f25: Factor = Factor.newFactor(newVarSet(x2, x5), factorCoeffs)
    val f36: Factor = Factor.newFactor(newVarSet(x3, x6), factorCoeffs)
    val f45: Factor = Factor.newFactor(newVarSet(x4, x5), factorCoeffs)
    val f56: Factor = Factor.newFactor(newVarSet(x5, x6), factorCoeffs)
    val factors: ImmutableList[Factor] = ImmutableList.of(f12, f14, f23, f25, f36, f45, f56)
    val builder: Nothing = new Nothing(classOf[VarSet], classOf[VarSet], false)
    builder.addNode(c1)
    builder.addNode(c2)
    builder.addNode(c3)
    builder.addNode(c4)
    builder.addNode(c5)
    builder.addNode(c6)
    builder.addNode(c7)
    builder.addEdge(c1, c2, c1.intersect(c2))
    builder.addEdge(c2, c3, c2.intersect(c3))
    builder.addEdge(c3, c4, c3.intersect(c4))
    builder.addEdge(c4, c5, c4.intersect(c5))
    builder.addEdge(c1, c6, c1.intersect(c6))
    builder.addEdge(c6, c7, c6.intersect(c7))
    builder.addEdge(c7, c5, c7.intersect(c5))
    val clusterGraph: ClusterGraphImpl = new ClusterGraphImpl(space, builder.build)
    assertEquals("DataGraphImpl{isDirected=false, [{x1, x2}, {x1, x4}, {x2, x3}, {x2, x5}, {x3, x6}, {x4, x5}, {x5, x6}], [\n" + "    {x1, x2}<->{x2, x5}:{x2}\n" + "    {x1, x4}<->{x1, x2}:{x1}\n" + "    {x1, x4}<->{x4, x5}:{x4}\n" + "    {x2, x3}<->{x3, x6}:{x3}\n" + "    {x2, x5}<->{x2, x3}:{x2}\n" + "    {x3, x6}<->{x5, x6}:{x6}\n" + "    {x4, x5}<->{x5, x6}:{x5}\n" + "]}", clusterGraph.toString)
    val result: Nothing = new Nothing(Iterables.toArray(factors, classOf[Factor]), clusterGraph, 100)
    val probability: Double = result.getProbability(new VarAssignment(Array[Var](x4, x5), Array[Int](1, 1)))
    assertEquals(0.4545, probability, 0.01)
*/
  }
}