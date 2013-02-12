package com.mikea.bayes.query

import com.mikea.bayes.BayesianNetwork
import com.mikea.bayes.Factor
import com.mikea.bayes.SumProduct
import com.mikea.bayes.VarAssignment
import com.mikea.bayes.VarSet
//remove if not needed

object QueryAlgorithm {
  var VAR_ELIMINATION: VarElimination = new VarElimination(new SumProduct.MinNeighborsStrategy())
  var DEFAULT = VAR_ELIMINATION

  abstract class Result {

    def query(query: VarSet, evidence: VarAssignment): Factor

    def getProbability(evidence: VarAssignment): Double = ???/*{
      val factor = query(VarSet.newVarSet(evidence.getVars), null)
      factor.getValue(evidence.getVars, evidence.getValues)
    }*/

    def query(q: VarSet): Factor = query(q, null)
  }
}
/**
 * @author mike.aizatsky@gmail.com
 */
trait QueryAlgorithm[R <: QueryAlgorithm.Result] {
  def prepare(network: BayesianNetwork): R
}
