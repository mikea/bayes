package com.mikea.bayes

class FactorProduct(val factors: Seq[Factor]) {
  def compute(): Factor = {
    var result = factors(0)
    for (i <- 1 until factors.length) {
      result = result.product(factors(i))
    }
    result
  }

  def computeAt(assignment: VarAssignment): Double = {
    var result = 1.0
    for (factor <- factors) {
      val value = factor.getValue(assignment)
      result *= value
    }
    result
  }
}
