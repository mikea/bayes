package com.mikea.bayes

import org.gga.graph.Graph
import org.gga.graph.impl.SparseGraphImpl
import org.gga.graph.sort.TopologicalSort
import org.gga.graph.util.IntQueue
import java.util.BitSet
import com.mikea.bayes.VarSet.newVarSet


object DSeparation {

  /**
   * Find all variables that are d-separated from sourceVariable given observations.
   */
  def findDSeparation(network: BayesianNetwork, sourceVariable: Var, observations: VarSet): VarSet = {
    val observationBitSet = new BitSet()
    for (observation <- observations) {
      observationBitSet.set(network.getVarIndex(observation).get)
    }
    val bitSet = findDSeparated(network.intGraph, network.getVarIndex(sourceVariable).get, observationBitSet)
    var vars = List.empty[Var]
    var i = bitSet.nextSetBit(0)
    while (i >= 0) {
      vars = vars :+ network.getVar(i)
      i = bitSet.nextSetBit(i + 1)
    }
    newVarSet(vars)
  }

  /**
   * Find all variables that are d-separated from sourceVariable given observations.
   */
  private def findDSeparated(g: Graph, sourceVariable: Int, observations: BitSet): BitSet = {
    val activeNodes = new BitSet()
    activeNodes.or(observations)
    val topologicalSort = TopologicalSort.sort(g)
    val i = topologicalSort.length - 1
    while (i >= 0) {
      val v = topologicalSort(i)
      for (edge <- g.edges(v) if activeNodes.get(edge.w)) {
        activeNodes.set(v)
      }
      i
    }
    val undirectedGraph = SparseGraphImpl.copyOf(g, isDirected = false)
    val visitedUp = new BitSet()
    val visitedDown = new BitSet()
    val reachable = new BitSet()
    val V = undirectedGraph.V
    val vertexQueue = new IntQueue()
    vertexQueue.push(sourceVariable)
    while (!vertexQueue.isEmpty) {
      var v = vertexQueue.pop()
      var direction = 1
      if (v >= V) {
        v -= V
        direction = -1
      }
      val visited = if (direction > 0) visitedUp else visitedDown
      if (visited.get(v)) //continue
        if (!observations.get(v)) {
          reachable.set(v)
        }
      visited.set(v)
      if (direction > 0 && !observations.get(v)) {
        for (edge <- undirectedGraph.edges(v)) {
          if (edge.v == v) {
            vertexQueue.push(edge.w + V)
          } else {
            vertexQueue.push(edge.v)
          }
        }
      } else if (direction < 0) {
        if (!observations.get(v)) {
          for (edge <- g.edges(v)) {
            vertexQueue.push(edge.w + V)
          }
        }
        if (activeNodes.get(v)) {
          for (edge <- undirectedGraph.edges(v) if edge.v != v) {
            vertexQueue.push(edge.v)
          }
        }
      }
    }
    reachable.flip(0, V)
    reachable
  }

  def findAllDSeparatedPairs(network: BayesianNetwork, observation: VarSet): Iterable[(Var, Var)] = {
    var result = List.empty[(Var, Var)]
    for (i <- 0 until network.intGraph.V) {
      val v1 = network.getVar(i)
      if (observation.contains(v1)) {
        //continue
      }
      val dSeparation = findDSeparation(network, v1, observation)
      for (j <- i + 1 until network.intGraph.V) {
        val v2 = network.getVar(j)
        if (observation.contains(v2)) {
          //continue
        }
        if (dSeparation.contains(v2)) {
          result = result :+ (v1, v2)
        }
      }
    }
    result
  }
}
