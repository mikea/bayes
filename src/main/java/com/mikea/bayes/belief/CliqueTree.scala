package com.mikea.bayes.belief

import com.mikea.bayes._


object CliqueTree {

  object Options {

    def defaultOptions(): Options = new Options()
  }

  class Options {

    var strategy: SumProduct.VarOrderStrategy = SumProduct.DEFAULT_STRATEGY

    private var prune: Boolean = true

    def withStrategy(strategy: SumProduct.VarOrderStrategy): Options = {
      this.strategy = strategy
      this
    }

    def dontPrune(): Options = {
      prune = false
      this
    }
  }

  def buildCliqueTree(network: BayesianNetwork): ClusterGraph = ???/*{
    buildCliqueTree(network.varList, network.getFactorList)
  }*/

  def buildCliqueTree(network: BayesianNetwork, options: Options): ClusterGraph = ???/*{
    buildCliqueTreeFromFactors(network.varList, network.getFactorList, options)
  }*/

  def buildCliqueTree(vars: Iterable[Var], factors: List[Factor]): ClusterGraph = ???/*{
    buildCliqueTreeFromFactors(vars, factors, new Options())
  }*/

  private def buildCliqueTreeFromFactors(vars: java.lang.Iterable[Var], factors: List[Factor], options: Options): ClusterGraph = ???/* {
    val factorScopes = Factor.getScopes(factors)
    buildCliqueTree(vars, factorScopes, options)
  }*/

  def buildCliqueTree(vars: java.lang.Iterable[Var], factorScopes: java.lang.Iterable[VarSet], options: Options): ClusterGraph = ???/*{
    val space = ProbabilitySpace.fromVars(vars)
    val scopes = newArrayList(Lists.transform(newArrayList(factorScopes), new Function[VarSet, VarSetHolder]() {

      override def apply(input: VarSet): VarSetHolder = {
        return new VarSetHolder(checkNotNull(input), null)
      }
    }))
    val builder = new ClusterGraphImpl.Builder(space, false)
    val varsToEliminate = newLinkedHashSet(vars)
    while (!varsToEliminate.isEmpty) {
      val `var` = options.strategy.pickVar(space, varsToEliminate, Lists.transform(scopes, new Function[VarSetHolder, VarSet]() {

        override def apply(input: VarSetHolder): VarSet = return checkNotNull(input).varSet
      }))
      varsToEliminate.remove(`var`)
      var clique = newVarSet(`var`)
      val srcs = newArrayList()
      var i = scopes.iterator()
      while (i.hasNext) {
        val holder = i.next()
        val scope = holder.varSet
        if (scope.contains(`var`)) {
          i.remove()
          clique = clique.add(scope)
          if (holder.clique != null) {
            srcs.add(holder.clique)
          }
        }
      }
      builder.addNode(clique)
      for (src <- srcs) {
        builder.addEdge(src, clique, VarSet.intersect(src, clique))
      }
      val newScope = clique.removeVars(`var`)
      if (!newScope.isEmpty) {
        scopes.add(new VarSetHolder(newScope, clique))
      }
    }
    if (options.prune) {
      PruneClusterGraph.prune(builder.build())
    } else {
      builder.build()
    }
  }*/

  private class VarSetHolder private (val varSet: VarSet, val clique: VarSet)
  {

    override def toString(): String = {
      "VarSetHolder{" + "varSet=" + varSet + ", clique=" + clique +
        '}'
    }
  }
}
