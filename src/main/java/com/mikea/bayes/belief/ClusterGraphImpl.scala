package com.mikea.bayes.belief

import com.mikea.bayes.ProbabilitySpace
import com.mikea.bayes.VarSet
import org.gga.graph.impl.DataGraphImpl

object ClusterGraphImpl {

  class Builder(val space: ProbabilitySpace, isDirected: Boolean) extends DataGraphImpl.Builder[VarSet, VarSet](isDirected) {

    ???
/*
    def build(): ClusterGraph = {
      new ClusterGraphImpl(space, super.build())
    }
*/
  }
}

/**
 * @author mike.aizatsky@gmail.com
 */
class ClusterGraphImpl(val space: ProbabilitySpace, size: Int) extends DataGraphImpl[VarSet, VarSet](size, false) with ClusterGraph {

  ???
/*
  def this(space: ProbabilitySpace, graph: DataGraph[VarSet, VarSet]) {
    super(graph)
    this.space = space
  }
*/


  def probabilitySpace = space
}
