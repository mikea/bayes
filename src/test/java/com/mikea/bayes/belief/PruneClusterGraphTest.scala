package com.mikea.bayes.belief

import org.junit.Test

/**
 * @author mike.aizatsky@gmail.com
 */
class PruneClusterGraphTest {
  @Test def testDoesntKillGraph {
    ???
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val a: Var = space.newVar("a", 2)
    val b: Var = space.newVar("b", 2)
    val c: Var = space.newVar("c", 2)
    val abSet: VarSet = newVarSet(a, b)
    val acSet: VarSet = newVarSet(a, c)
    val aSet: VarSet = newVarSet(a)
    val builder: Nothing = new Nothing(classOf[VarSet], classOf[VarSet], false)
    builder.addNode(abSet).addNode(acSet).addNode(aSet).addEdge(aSet, abSet, aSet).addEdge(aSet, acSet, aSet)
    val prunedGraph: ClusterGraph = PruneClusterGraph.prune(new ClusterGraphImpl(space, builder.build))
    assertEquals("DataGraphImpl{isDirected=false, [{a, b}, {a, c}], [\n" + "    {a, b}<->{a, c}:{a}\n" + "]}", prunedGraph.toString)
*/
  }
}