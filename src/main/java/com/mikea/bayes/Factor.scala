package com.mikea.bayes

import org.gga.graph.maps.DataGraph
import scala.collection.mutable
import org.gga.graph.impl.DataGraphImpl

object Factor {

  def newFactor(varSet: VarSet, values: Array[Double]): Factor = new Factor(varSet, values)

  def newFactor(vars: Iterable[Var], doubles: Array[Double]): Factor = newFactor(VarSet.newVarSet(vars), doubles)

/*
  def product(factors: Factor*): Factor = {
    if (factors.length == 1) return factors(0)
    product(factors:Iterable[Factor])
  }
*/

  def product(factors: Seq[Factor]): Factor = {
    if (factors.size == 1) return factors(0)
    product(factors:Iterable[Factor])
  }

  def product(factors: Iterable[Factor]): Factor = {
    val varSets = factors map (f => f.scope)
    val productVarSet = VarSet.union(varSets)
    val numValues = productVarSet.cardinality
    val values = Array.fill(numValues)(1.0)
    for (factor <- factors) {
      val varSet = factor.scope
      productVarSet.scanWith(varSet, new VarSet.WithScanner() {

        override def scan(index1: Int, index2: Int) {
          values(index1) *= factor.values(index2)
        }
      })
    }
    newFactor(productVarSet, values)
  }

  def getScopes(factors: Iterable[Factor]): Iterable[VarSet] = factors.map(f => f.scope)

  def withVariables(vars: Var*): Builder = new Builder().withVariables(vars)

  def induceMarkovNetwork(factors: Seq[Factor]): DataGraph[Var, List[Factor]] = {
    val vars = VarSet.union(getScopes(factors))
    // todo: use builder
    val graph = new DataGraphImpl[Var, List[Factor]](vars.size, false)
    var i = 0
    for (v <- vars) {
      graph.setNode(i, v)
      i += 1
    }

    for (v <- 0 until graph.V) {
      val vVar = graph.node(v)
      for (w <- v + 1 until graph.V) {
        val wVar = graph.node(w)

        var edgeFactors: Vector[Factor] = Vector()
        for (factor <- factors) {
          val scope = factor.scope
          if (scope.contains(vVar) && scope.contains(wVar)) {
            edgeFactors :+= factor
          }
        }
        if (!edgeFactors.isEmpty) {
          assert(v != w)
          graph.insert(v, w, edgeFactors.toList)
        }
      }
    }
    graph
  }

  def constant(node: VarSet, v: Double): Factor = {
    new Factor(node, Array.fill(node.cardinality)(v))
  }

  class Builder {

    private var varSet: VarSet = null

    private var values: Array[Double] = null

    def withVariables(vars: Seq[Var]): Builder = {
      varSet = VarSet.newVarSet(vars)
      values = new Array[Double](varSet.cardinality)
      this
    }

    def row(`var`: Var, assignmentBuilder: VarAssignment.Builder, values: Array[Double]): Builder = {
      require(values.length == `var`.cardinality)
      val assignment = assignmentBuilder.build()
      require(!assignment.contains(`var`))
      for (value <- 0 until `var`.cardinality) {
        val a = assignment.set(`var`, value)
        val idx = varSet.getIndex(a)
        this.values(idx) = values(value)
      }
      this
    }

    def build(): Factor = newFactor(varSet, values)

    def add(at: VarAssignment.Builder, value: Double): Builder = {
      val assignment = at.build()
      val idx = varSet.getIndex(assignment)
      values(idx) += value
      this
    }

    def set(at: VarAssignment.Builder, value: Double): Builder = {
      val assignment = at.build()
      values(varSet.getIndex(assignment)) = value
      this
    }

    def uniform(v: Double): Builder = {
      values = Array.fill(values.length)(v)
      this
    }

    def row(`var`: Var,
            at: VarAssignment.Builder,
            valueOrder: Array[String],
            doubles: Array[Double]): Builder = {
      val assignment = at.build()
      for (i <- 0 until valueOrder.length) {
        val value = valueOrder(i)
        val d = doubles(i)
        val a = assignment.set(`var`, value)
        values(varSet.getIndex(a)) = d
      }
      this
    }
  }
}

class Factor (val scope: VarSet, val values: Array[Double]) {
  require(scope.cardinality == values.length)

  override def toString: String = { "Factor(" + scope + ", " + values.mkString("[", ", ", "]") + ")" }

  def toString(valueFormat: String): String = { "Factor(" + scope + ", " + values.map(v => valueFormat.format(v)) + ")"
  }

  def toStringAsTable(valueFormat: String): String = {
    val result = new StringBuilder()
    result.append("Factor(")
    result.append(scope)
    result.append("):\n")
    for (assignment <- scope.assignments()) {
      result.append(assignment.toString())
      result.append(": ")
      result.append(valueFormat.format(getValue(assignment)))
      result.append("\n")
    }
    result.toString()
  }

  def toStringAsTable(columnVar: Var, valueFormat: String): String = {
    val result = new StringBuilder()
    result.append("Factor(")
    result.append(scope)
    result.append("):\n")
    val rowsVarSet = scope.removeVars(List(columnVar))
    for (rowAssignment <- rowsVarSet.assignments()) {
      result.append(rowAssignment.toString())
      result.append(": ")
      for (i <- 0 until columnVar.cardinality) {
        if (i > 0) result.append(" ")
        val assignment = rowAssignment.set(columnVar, i)
        result.append(valueFormat.format(getValue(assignment)))
      }
      result.append("\n")
    }
    result.toString()
  }

  def product(f2: Factor): Factor = Factor.product(List(this, f2))

  def sum(): Double = {
    var result = 0.0
    for (value <- values) {
      result += value
    }
    result
  }

  def normalize(): Factor = {
    val s = sum()
    assert(s != 0)
    val newValues = new Array[Double](values.length)
    for (i <- 0 until values.length) {
      newValues(i) = values(i) / s
    }
    new Factor(scope, newValues)
  }

  /**
   * Sum out vars.
   */
  def marginalize(vars: VarSet): Factor = {
    if (vars.isEmpty) return this
    val newVarSet = scope.removeVars(vars)
    val newValues = new Array[Double](newVarSet.cardinality)
    scope.scanWith(newVarSet, new VarSet.WithScanner() {

      override def scan(index1: Int, index2: Int) {
        newValues(index2) += values(index1)
      }
    })
    new Factor(newVarSet, newValues)
  }

  def observeEvidence(observedVariables: Seq[Var], observedValues: Seq[Int]): Factor = {
    require(observedValues.length == observedVariables.length)
    if (observedValues.length == 0) {
      return this
    }
    val newValues = new Array[Double](values.length)
    var allowedValues: Map[Var, mutable.BitSet] = Map()

    for (observedVariable <- observedVariables) {
      allowedValues += observedVariable -> mutable.BitSet.empty
    }
    for (i <- 0 until observedVariables.length) {
      allowedValues(observedVariables(i)) += observedValues(i)
    }
    for (i <- 0 until values.length) {
      val assignment = scope.getAssignment(i)
      var consistent = true
      for (v <- assignment) {
        val value = assignment.get(v)
        val allowedValue = allowedValues.get(v)
        if (value >= 0 && allowedValue != null && !allowedValue.isEmpty && !allowedValue.get(value)) {
          consistent = false
          // break
        }
      }
      if (consistent) {
        newValues(i) = values(i)
      }
    }
    new Factor(scope, newValues)
  }

  def getValue(assignment: VarAssignment): Double = values(scope.getIndex(assignment))

  def getValue(vars: Array[Var], values: Array[Int]): Double = {
    getValue(new VarAssignment(vars, values))
  }

  def getValue(builder: VarAssignment.Builder): Double = getValue(builder.build())

  /**
   * Normalize value for every possible assignment of vars.
   */
  def normalizeBy(vars: Var*): Factor = {
    val scale = marginalize(scope.removeVars(vars))
    val newValues = new Array[Double](values.length)
    for (i <- 0 until values.length) {
      val assignment = scope.getAssignment(i)
      newValues(i) = values(i) / scale.getValue(assignment)
    }
    new Factor(scope, newValues)
  }

  def equals(other: Factor, epsilon: Double): Boolean = {
    if (scope != other.scope) return false
    for (i <- 0 until values.length if Math.abs(values(i) - other.values(i)) > epsilon) return false
    true
  }

  def observeEvidence(evidence: VarAssignment): Factor = {
    if (evidence == null) return this
    observeEvidence(evidence.getVars, evidence.values)
  }
}
