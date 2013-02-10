package com.mikea.bayes.benchmarks

import com.google.caliper.Runner
import com.google.caliper.SimpleBenchmark
import com.mikea.bayes.Factor

/**
 * @author mike.aizatsky@gmail.com
 */
object FactorProductBenchmark {
  def main(args: Array[String]) {
    Runner.main(classOf[FactorProductBenchmark], args)
  }
}

class FactorProductBenchmark extends SimpleBenchmark {
  protected override def setUp {
    ???
/*
    super.setUp
    val space: ProbabilitySpace = new ProbabilitySpace
    val vars1: Collection[Var] = newArrayList
    val vars2: Collection[Var] = newArrayList
    {
      var i: Int = 0
      while (i < 10) {
        {
          vars1.add(space.newVar("a" + i, 2))
          vars2.add(space.newVar("b" + i, 2))
        }
        ({
          i += 1; i
        })
      }
    }
    factor1 = Factor.constant(VarSet.newVarSet(vars1), 1)
    factor2 = Factor.constant(VarSet.newVarSet(vars2), 1)
*/
  }

  @SuppressWarnings(Array("UnusedDeclaration")) def time1(reps: Int) {
    {
      var i: Int = 0
      while (i < reps) {
        {
          val f: Factor = factor1.product(factor2)
        }
        ({
          i += 1; i
        })
      }
    }
  }

  private var factor1: Factor = null
  private var factor2: Factor = null
}