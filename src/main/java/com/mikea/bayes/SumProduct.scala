package com.mikea.bayes

import scala.collection.mutable


object SumProduct {

  val DEFAULT_STRATEGY = new MinNeighborsStrategy()

  def sumProductVariableElimination(vars: Iterable[Var], factors: Seq[Factor]): Factor = {
    sumProductVariableElimination(vars, factors, DEFAULT_STRATEGY)
  }

  def sumProductVariableElimination(vars: Iterable[Var], factors: Seq[Factor], strategy: VarOrderStrategy): Factor = {
    sumProductVariableElimination(ProbabilitySpace.fromVars(vars), vars, factors, strategy)
  }

  private def sumProductVariableElimination(space: ProbabilitySpace,
                                            vars: Iterable[Var],
                                            factors: Seq[Factor],
                                            strategy: VarOrderStrategy): Factor = {
    var varSet: Set[Var] = vars.toSet
    var currentFactors: Seq[Factor] = factors
    while (!varSet.isEmpty) {
      println("varSet.size() = " + varSet.size)
      val v = strategy.pickVar(space, varSet, Factor.getScopes(currentFactors))
      currentFactors = sumProductEliminateVar(currentFactors, v)
      varSet -= v
    }
    Factor.product(currentFactors)
  }

  private def sumProductEliminateVar(factors: Iterable[Factor], v: Var): List[Factor] = {
    var result = List[Factor]()
    var product = List[Factor]()
    for (factor <- factors) {
      val scope = factor.scope
      if (scope.contains(v)) {
        product :+= factor
      } else {
        result :+= factor
      }
    }
    val newFactor = Factor.product(product).marginalize(VarSet(v))
    result :+= newFactor
    result
  }

  trait VarOrderStrategy {
    def pickVar(space: ProbabilitySpace, vars: Set[Var], factorScopes: Iterable[VarSet]): Var
  }

  abstract class GreedyOrderStrategy extends VarOrderStrategy {
    def computeCosts(costs: Array[Long], vars: Set[Var], factorScopes: Iterable[VarSet])

    override def pickVar(space: ProbabilitySpace, vars: Set[Var], factorScopes: Iterable[VarSet]): Var = {
      val costs = Array.fill(space.numVars)(1l)
      computeCosts(costs, vars, factorScopes)
      var minVar: Var = null
      var minCost = Long.MaxValue
      for (v <- vars) {
        var cost = costs(v.getIndex)
        if (cost < 0) cost = Long.MaxValue
        if (cost < minCost) {
          minCost = cost
          minVar = v
        }
      }
      println("minCost = " + minCost)
      println("minVar = " + minVar)
      assert(minVar != null)
      minVar
    }
  }

  class MinNeighborsStrategy extends GreedyOrderStrategy {
    override def computeCosts(costs: Array[Long], vars: Set[Var], factorScopes: Iterable[VarSet]) {
      val neighbors = new mutable.HashMap[Var, mutable.Set[Var]]() with mutable.MultiMap[Var, Var]

      for (scope <- factorScopes; v1 <- scope; v2 <- scope) {
        neighbors.addBinding(v1, v2)
      }

      for (v <- neighbors.keySet) {
        costs(v.getIndex) = neighbors.get(v).size
      }
    }
  }

  class MinWeightStrategy extends GreedyOrderStrategy {

    override def computeCosts(costs: Array[Long], vars: Set[Var], factorScopes: Iterable[VarSet]) {
      for (scope <- factorScopes) {
        val factorCardinality = scope.cardinality
        assert(factorCardinality != 0, s"Zero cardinality factor: $scope")
        for (v <- scope) {
          costs(v.getIndex) *= factorCardinality
        }
      }
    }
  }

  class MinFillStrategy extends GreedyOrderStrategy {

    override def computeCosts(costs: Array[Long], vars: Set[Var], factorScopes: Iterable[VarSet]) {
      ???
/*
      val neighbors = HashMultimap.create()
      for (scope <- factorScopes; `var` <- scope) {
        neighbors.putAll(`var`, scope)
      }
      for (`var` <- neighbors.keySet) {
        val varNeighbors = neighbors.get(`var`)
        var count = 0
        for (var1 <- varNeighbors; var2 <- varNeighbors) {
          if (var1 == var2 || var1 == `var` || var2 == `var`) //continue
            if (!neighbors.containsEntry(var1, var2)) {
              count += 1
            }
        }
        costs(`var`.getIndex) = count / 2
      }
*/
    }
  }
}
