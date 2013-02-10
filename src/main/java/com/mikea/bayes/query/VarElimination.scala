package com.mikea.bayes.query

import com.mikea.bayes.BayesianNetwork
import com.mikea.bayes.Factor
import com.mikea.bayes.SumProduct
import com.mikea.bayes.VarAssignment
import com.mikea.bayes.VarSet
import com.mikea.bayes.query.QueryAlgorithm.Result

//remove if not needed

/**
 * @author mike.aizatsky@gmail.com
 */
class VarElimination(val strategy: SumProduct.VarOrderStrategy) extends QueryAlgorithm[QueryAlgorithm.Result] {

  override def prepare(network: BayesianNetwork): Result = {
    new Result() {

      override def query(query: VarSet, evidence: VarAssignment): Factor = queryImpl(network, query, evidence)
    }
  }

  private def queryImpl(network: BayesianNetwork, query: VarSet, evidence: VarAssignment): Factor = ???/*{
    val factors = newArrayList()
    for (factor <- network.getFactors) {
      factors.add(factor.observeEvidence(evidence))
    }
    val varsToEliminate = Sets.difference(network.varSet, query.varSet)
    sumProductVariableElimination(varsToEliminate, factors, strategy)
      .normalize()
  }*/
}
