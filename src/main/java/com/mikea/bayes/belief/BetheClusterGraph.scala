package com.mikea.bayes.belief

import com.mikea.bayes.VarSet

object BetheClusterGraph {

  def buildBetheClusterGraph(varSets: List[VarSet]): ClusterGraph = ??? /*{
    val individualVarSets = newHashMap()
    for (`var` <- VarSet.union(varSets)) {
      individualVarSets.put(`var`, VarSet.newVarSet(`var`))
    }
    val uniqueVarSets = newHashSet(Iterables.filter(varSets, new Predicate[VarSet]() {

      override def apply(varSet: VarSet): Boolean = return checkNotNull(varSet).size != 1
    }))
    val filteredVarSets = newArrayList(Iterables.filter(varSets, new Predicate[VarSet]() {

      override def apply(input: VarSet): Boolean = return uniqueVarSets.contains(input)
    }))
    val result = new ClusterGraphImpl(ProbabilitySpace.fromVarSets(varSets), filteredVarSets.size + individualVarSets.size)
    var idx = 0
    for (varSet <- filteredVarSets) {
      result.setNode(idx, varSet)
      idx += 1
    }
    for (varSet <- individualVarSets.values) {
      result.setNode(idx, varSet)
      idx += 1
    }
    for (varSet <- filteredVarSets; `var` <- varSet) {
      val individualVarSet = individualVarSets.get(`var`)
      result.insert(varSet, individualVarSet, individualVarSet)
    }
    result
  }*/
}
