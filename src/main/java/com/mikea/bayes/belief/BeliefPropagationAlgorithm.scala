package com.mikea.bayes.belief

import com.mikea.bayes.query.QueryAlgorithm
import com.mikea.bayes.{BayesianNetwork, VarAssignment, VarSet, Factor}
import org.gga.graph.maps.EdgeMap
import gnu.trove.map.TIntObjectMap
import org.gga.graph.Edge
import org.gga.graph.tree.Trees
import com.mikea.bayes.belief.BeliefPropagationAlgorithm.BPResult

object BeliefPropagationAlgorithm {

  class BPResult(val iterations: Int) extends QueryAlgorithm.Result {

    private val factors: Array[Factor] = ???

    private val clusterGraph: ClusterGraph = ???

    private var messages1: EdgeMap[Factor] = ???

    private var messages2: EdgeMap[Factor] = ???

    private var potentials: TIntObjectMap[Factor] = ???

    ???
/*
    def this(factors: Array[Factor], clusterGraph: ClusterGraph, iterations: Int) {
      this()
      ???
/*
      this.factors = factors
      this.clusterGraph = clusterGraph
      this.iterations = iterations
      iterate()
*/
    }
*/

/*
    private def iterate() {
      messages1 = new EdgeMapImpl[Factor]()
      messages2 = new EdgeMapImpl[Factor]()
      potentials = new TIntObjectHashMap[Factor]()
      assignFactorsToClusters()
      val edges = Graphs.getAllEdges(clusterGraph.intGraph)
      passMessages(edges)
      computeFinalPotentials(edges)
    }
*/

    private def passMessages(edges: Iterable[Edge]) {
      val i = getDirectionIterator(edges)
      while (i.hasNext) {
        val direction = i.next()
        sendMessage(direction.from, direction.to, direction.edge)
      }
    }

    private def getDirectionIterator(edges: Iterable[Edge]): Iterator[MessageDirection] = {
      if (Trees.isTree(clusterGraph.intGraph, 0)) {
        getTreeIterator
      } else {
        getRandomEdgeOrderIterator(edges)
      }
    }

    private def getTreeIterator(): Iterator[MessageDirection] = ??? /*{
      val edgesInOrder = newArrayList()
      clusterGraph.intGraph.getDfs.depthFirstSearch(new AbstractDfsVisitor() {

        override def treeEdge(e: Edge, graph: Graph) {
          edgesInOrder.add(e)
        }

        override def backEdge(e: Edge, graph: Graph) {
          throw new IllegalStateException()
        }
      })
      val result = newArrayList()
      for (edge <- Lists.reverse(edgesInOrder)) {
        result.add(new MessageDirection(edge.w(), edge.v(), edge))
      }
      for (edge <- edgesInOrder) {
        result.add(new MessageDirection(edge.v(), edge.w(), edge))
      }
      result.iterator()
    }*/

    private def getRandomEdgeOrderIterator(edges: Iterable[Edge]): Iterator[MessageDirection] =  ??? /*{
      val edgeList = newArrayList(edges)
      new AbstractIterator[MessageDirection]() {

        private var direction: Boolean = false

        private var iteration: Int = 0

        private var i: Iterator[Edge] = null

        protected override def computeNext(): MessageDirection = {
          if (i == null || !i.hasNext) {
            if (iteration > iterations * 2) {
              endOfData()
            }
            direction = !direction
            if (direction) {
              Collections.shuffle(edgeList)
              i = edgeList.iterator()
            } else {
              i = Lists.reverse(edgeList).iterator()
            }
            iteration
          }
          val edge = i.next()
          val from = if (direction) edge.v() else edge.w()
          val to = if (direction) edge.w() else edge.v()
          new MessageDirection(from, to, edge)
        }
      }
    }
*/
/*
    private def computeFinalPotentials(edges: java.lang.Iterable[Edge]) {
      for (edge <- edges) {
        incorporateMessage(edge.v(), edge.w(), edge)
        incorporateMessage(edge.w(), edge.v(), edge)
      }
    }
*/

 /*   private def assignFactorsToClusters() {
      nextFactor: for (factor <- factors) {
        for (i <- 0 until clusterGraph.V() if clusterGraph.node(i).containsAll(factor.getScope)) {
          var potential = potentials.get(i)
          potential = if (potential == null) factor else potential.product(factor)
          potentials.put(i, potential)
          //continue
        }
        throw new IllegalStateException()
      }
      for (i <- 0 until clusterGraph.V()) {
        if (potentials.containsKey(i)) //continue
          potentials.put(i, Factor.constant(clusterGraph.node(i), 1))
      }
    }

    private def incorporateMessage(from: Int, to: Int, edge: Edge) {
      val message = getMessage(from, to, edge)
      var potential = potentials.get(to)
      potential = potential.product(checkNotNull(message))
      potentials.put(to, potential)
    }
*/
    private def sendMessage(from: Int, to: Int, edge: Edge) {
      var newMessage = potentials.get(from)
      for (e <- clusterGraph.intGraph.edges(from) if e.other(from) != to) {
        val incomingMessage = getMessage(e.other(from), from, e)
        if (incomingMessage != null) {
          newMessage = newMessage.product(incomingMessage)
        }
      }
      val toVars = clusterGraph.node(to)
      val fromVars = clusterGraph.node(from)
      val commonVars = fromVars.intersect(toVars)
      val varsToEliminate = fromVars.removeVars(commonVars)
      newMessage = newMessage.marginalize(varsToEliminate)
      newMessage = newMessage.normalize()
      setMessage(newMessage, from, to, edge)
    }

    private def setMessage(newMessage: Factor,
                           from: Int,
                           to: Int,
                           edge: Edge) {
      if (from > to) messages1.put(edge, newMessage) else messages2.put(edge, newMessage)
    }

    private def getMessage(v: Int, w: Int, edge: Edge): Factor = ??? /*{
      if (v > w) messages1.get(edge) else messages2.get(edge)
    }*/

    override def query(query: VarSet, evidence: VarAssignment): Factor = {
      assert(evidence == null, "Not implemented")
      for (i <- 0 until clusterGraph.V) {
        val varSet = clusterGraph.node(i)
        if (varSet.containsAll(query)) {
          var potential = potentials.get(i)
          val varsToMarginalize = potential.scope.removeVars(query)
          potential = potential.marginalize(varsToMarginalize)
          return potential.normalize()
        }
      }
      throw new UnsupportedOperationException("No cluster node covering evidence. Not implemented.")
    }
  }

  private class MessageDirection private (val from: Int, val to: Int, val edge: Edge)

}

/**
 * @author mike.aizatsky@gmail.com
 */
abstract class BeliefPropagationAlgorithm protected (protected val iterations: Int)
  extends QueryAlgorithm[BeliefPropagationAlgorithm.BPResult] {

  override def prepare(network: BayesianNetwork): BPResult
}
