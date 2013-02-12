package com.mikea.bayes

import org.gga.graph.Graph
import org.gga.graph.impl.SparseGraphImpl
import org.gga.graph.sort.TopologicalSort
import org.gga.graph.util.IntQueue
import com.mikea.bayes.VarSet.newVarSet
import scala.collection.mutable


object DSeparation {

  /**
   * Find all variables that are d-separated from sourceVariable given observations.
   */
  def findDSeparation(network: BayesianNetwork, sourceVariable: Var, observations: VarSet): VarSet = {
    val observationBitSet = mutable.BitSet()
    for (observation <- observations) {
      observationBitSet += network.getVarIndex(observation).get
    }
    val bitSet = findDSeparated(network.intGraph, network.getVarIndex(sourceVariable).get, observationBitSet)
    var vars = List.empty[Var]
    for (i <- bitSet) {
      vars :+= network.getVar(i)
    }
    newVarSet(vars)
  }

  /**
   * Find all variables that are d-separated from sourceVariable given observations.
   */
  private def findDSeparated(g: Graph, sourceVariable: Int, observations: mutable.BitSet): mutable.BitSet = {
    val activeNodes = mutable.BitSet()
    observations.foreach(activeNodes.add(_))
    val topologicalSort = TopologicalSort.sort(g)

    for (v <- topologicalSort.reverse) {
      for (edge <- g.edges(v) if activeNodes.contains(edge.w)) {
        activeNodes += v
      }
    }
    val undirectedGraph = SparseGraphImpl.copyOf(g, isDirected = false)
    val visitedUp = mutable.BitSet()
    val visitedDown = mutable.BitSet()
    val reachable = mutable.BitSet()
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
      if (!visited.contains(v)) {
        if (!observations.contains(v)) {
          reachable += v
        }
        visited += v
        if (direction > 0 && !observations.contains(v)) {
          for (edge <- undirectedGraph.edges(v)) {
            if (edge.v == v) {
              vertexQueue.push(edge.w + V)
            } else {
              vertexQueue.push(edge.v)
            }
          }
        } else if (direction < 0) {
          if (!observations.contains(v)) {
            for (edge <- g.edges(v)) {
              vertexQueue.push(edge.w + V)
            }
          }
          if (activeNodes.contains(v)) {
            for (edge <- undirectedGraph.edges(v) if edge.v != v) {
              vertexQueue.push(edge.v)
            }
          }
        }
      } else {

      }
    }

    for (i <- 0 until V) {
      if (reachable.contains(i)) {
        reachable.remove(i)
      } else {
        reachable.add(i)
      }
    }
    reachable
  }

  def findAllDSeparatedPairs(network: BayesianNetwork, observation: VarSet): Iterable[(Var, Var)] = {
    var result = List.empty[(Var, Var)]

    for (i <- 0 until network.intGraph.V) {
      val v1 = network.getVar(i)
      if (!observation.contains(v1)) {
        val dSeparation = findDSeparation(network, v1, observation)
        for (j <- i + 1 until network.intGraph.V) {
          val v2 = network.getVar(j)
          if (!observation.contains(v2)) {
            if (dSeparation.contains(v2)) {
              result :+= (v1, v2)
            }
          }
        }
      }
    }
    result
  }
}
