package com.mikea.bayes.query

import com.mikea.bayes.BayesianNetwork
import com.mikea.bayes.Factor
import com.mikea.bayes.SumProduct
import com.mikea.bayes.VarAssignment
import com.mikea.bayes.VarSet
import com.mikea.bayes.query.QueryAlgorithm.Result
import scala.collection.mutable

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

  private def queryImpl(network: BayesianNetwork, query: VarSet, evidence: VarAssignment): Factor = {
    var factors = mutable.ArrayBuffer.empty[Factor]
    for (factor <- network.factors) {
      factors += factor.observeEvidence(evidence)
    }
    val varsToEliminate = network.varSet.diff(query.varSet)
    SumProduct.sumProductVariableElimination(varsToEliminate, factors, strategy).normalize()
  }
}
