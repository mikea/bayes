package com.mikea.bayes.belief

import com.mikea.bayes.BayesianNetwork
import com.mikea.bayes.belief.BeliefPropagationAlgorithm.BPResult

/**
 * @author mike.aizatsky@gmail.com
 */
class CliqueTreeQueryAlgorithm(iterations: Int) extends BeliefPropagationAlgorithm(iterations) {

  override def prepare(network: BayesianNetwork): BPResult = ???/*{
    new BPResult(network.getFactors, CliqueTree.buildCliqueTree(network), iterations)
  }*/
}
