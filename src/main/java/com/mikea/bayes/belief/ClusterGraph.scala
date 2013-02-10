package com.mikea.bayes.belief

import com.mikea.bayes.ProbabilitySpace
import com.mikea.bayes.VarSet
import org.gga.graph.maps.DataGraph

/**
 * @author mike.aizatsky@gmail.com
 */
trait ClusterGraph extends DataGraph[VarSet, VarSet] {
  def probabilitySpace: ProbabilitySpace

  def maxCardinality(): Long = {
    var maxCardinality = 0
    for (i <- 0 until V) {
      val varSet = node(i)
      val cardinality = varSet.cardinality
      if (maxCardinality < cardinality) {
        maxCardinality = cardinality
      }
    }
    maxCardinality
  }
}
