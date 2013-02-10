package com.mikea.bayes

import com.google.common.base.Preconditions
import org.gga.graph.Edge
import org.gga.graph.Graph
import org.gga.graph.impl.DataGraphImpl
import org.gga.graph.search.dfs.AbstractDfsVisitor
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Preconditions.checkState
import scala.collection.mutable

object BayesianNetwork {
  def withVariables(vars: Var*): Builder = new Builder().withVariables(vars)

  def withVariables(vars: Iterable[Var]): Builder = new Builder().withVariables(vars)

  class Builder {
    private var vars: List[Var] = List.empty[Var]
    private var edges: List[(Var, Var)] = List.empty
    private var factors: Map[Var, Factor] = Map.empty

    def withVariables(vars: Iterable[Var]): Builder = {
      this.vars = vars.toList
      this
    }

    def addVariable(`var`: Var): Builder = {
      vars = vars :+ `var`
      this
    }

    def build(): BayesianNetwork = {
      val size = vars.size
      val g = new DataGraphImpl[Var, Void](size, true)
      for (i <- 0 until size) {
        g.setNode(i, vars(i))
      }
      for (edge <- edges) {
        g.insert(edge._1, edge._2, null)
      }
      val factorArray = new Array[Factor](size)
      for (i <- 0 until size) {
        factorArray(i) = factors.get(vars(i)).get
      }
      new BayesianNetwork(g, factorArray)
    }

    def edge(from: Var, to: Var): Builder = {
      edges = edges :+ (from, to)
      this
    }

    def factor(`var`: Var, factor: Factor): Builder = {
      factors += `var` -> factor
      this
    }
  }
}

class BayesianNetwork private (val graph: DataGraphImpl[Var, Void],
                               val factors: Array[Factor])
{

  Preconditions.checkArgument(graph.isDirected)

  def validate() {
    checkState(factors.length == V)
    var factorVariables: Map[Var, mutable.Set[Var]] = Map()
    for (i <- 0 until V) {
      val v = getVar(i)
      factorVariables += v -> mutable.Set(v)
    }
    graph.intGraph.getDfs.depthFirstSearch(new AbstractDfsVisitor() {

      override def examineEdge(e: Edge, graph: Graph) {
        val w = getVar(e.w)
        val v = getVar(e.v)
        factorVariables(w) += v
      }
    })
    for (i <- 0 until factors.length) {
      val factor = factors(i)
      val `var` = getVar(i)
      validateFactor(i, factor, factorVariables(`var`).toSet)
    }
  }

  private def validateFactor(v: Int, factor: Factor, factorVariables: Set[Var]) {
    assert(factor.scope == factorVariables, s"Factor variables for vertex $v do not match graph structure. Expected: $factorVariables, actual: ${factor.scope}")

    val marginalizedFactor = factor.marginalize(VarSet(getVar(v)))
    val values = marginalizedFactor.values
    for (value <- values) {
      assert(1.0 == value, s"Marginalized distribution for $v should contain only 1, but is $marginalizedFactor.")
    }
  }

  def computeJointDistribution(): Factor = new FactorProduct(factors).compute()

  def computeProbability(assignment: VarAssignment): Double = {
    new FactorProduct(factors).computeAt(assignment)
  }

  def intGraph: Graph = graph.intGraph

  def getVar(j: Int): Var = checkNotNull(graph.node(j))

  def getVarIndex(observation: Var): Option[Int] = graph.getIndex(observation)

  def computeProbability(at: VarAssignment.Builder): Double = computeProbability(at.build())

  def varSet: Set[Var] = varList.toSet

  def V: Int = graph.V

  def varList: List[Var] = graph.nodes

  def getFactorList: List[Factor] = factors.toList

  def getVarByName(varName: String): Var = {
    for (i <- 0 until V if getVar(i).getName == varName) return getVar(i)
    throw new NoSuchElementException()
  }

  def getFactor(v: Var): Option[Factor] = getVarIndex(v) map (factors(_))
}
