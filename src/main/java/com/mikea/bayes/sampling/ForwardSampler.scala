package com.mikea.bayes.sampling

import com.mikea.bayes.BayesianNetwork
import com.mikea.bayes.VarAssignment
import org.gga.graph.sort.TopologicalSort
import java.util.Random
//remove if not needed

object ForwardSampler {

  def sample(network: BayesianNetwork): VarAssignment = {
    val random = new Random()
    val graph = network.intGraph
    val vertices = TopologicalSort.sort(graph)
    sample(network, random, vertices)
  }

  def sample(network: BayesianNetwork, samples: Int): Array[VarAssignment] = {
    val random = new Random()
    val graph = network.intGraph
    val vertices = TopologicalSort.sort(graph)
    val result = new Array[VarAssignment](samples)
    for (i <- 0 until result.length) {
      result(i) = sample(network, random, vertices)
    }
    result
  }

  private def sample(network: BayesianNetwork, random: Random, topoVertices: Array[Int]): VarAssignment = ???/*{
    val values = new Array[Int](topoVertices.length)
    Arrays.fill(values, -1)
    for (v <- topoVertices) {
      val `var` = network.getVar(v)
      val f = network.getFactor(`var`)
      val scope = f.getScope
      var builder = new VarAssignment.Builder()
      for (scopeVar <- scope) {
        if (scopeVar == `var`) //continue
        val idx = scopeVar.getIndex
        checkState(values(idx) != -1, "Wrong var order. var=%s scopeVar=%s", `var`, scopeVar)
        builder = builder.at(scopeVar, values(idx))
      }
      val assignment = builder.build()
      var r = random.nextDouble()
      for (j <- 0 until `var`.cardinality) {
        val p = f.getValue(assignment.set(`var`, j))
        r -= p
        if (r <= 0) {
          values(v) = j
          //break
        }
      }
      checkState(values(v) >= 0)
    }
    var builder = new VarAssignment.Builder()
    for (i <- 0 until values.length) {
      builder = builder.at(network.getVar(i), values(i))
    }
    builder.build()
  }*/
}
