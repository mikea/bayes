package com.mikea.bayes.belief

import com.mikea.bayes.Factor

object ClusterGraphs {

  def validate(clusterGraph: ClusterGraph, factors: Iterable[Factor]) {
    validateFamilyPreservation(clusterGraph, factors)
    validateRunningIntersection(clusterGraph)
  }

  private def validateRunningIntersection(clusterGraph: ClusterGraph) {
    ???
/*
    val probabilitySpace = clusterGraph.probabilitySpace
    for (v <- probabilitySpace.variables) {
      val filteredGraph = new SubGraph(clusterGraph.intGraph, new SubGraph.GraphFilter() {

        override def acceptVertex(v: Int): Boolean = {
          return clusterGraph.node(v).contains(`var`)
        }

        override def acceptEdge(e: Edge): Boolean = {
          return clusterGraph.edge(e).contains(`var`)
        }
      })
      if (filteredGraph.V() != 0 && !StrongComponents.isConnected(filteredGraph)) {
        throw new IllegalArgumentException("Running intersection property failed. Original graph: " +
          clusterGraph +
          " var: " +
          `var` +
          " filtered graph: " +
          filteredGraph)
      }
    }
*/
  }

  private def validateFamilyPreservation(clusterGraph: ClusterGraph, factors: Iterable[Factor]) {
    ???
/*
    nextFactor: for (factor <- factors) {
      for (v <- 0 until clusterGraph.V()) {
        val varSet = clusterGraph.node(v)
        if (varSet.containsAll(factor.getScope)) {
          //continue
        }
      }
      throw new IllegalArgumentException("Family preservation property failed.")
    }
*/
  }
}
