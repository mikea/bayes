
package com.mikea.bayes.benchmarks.query

import com.google.caliper.Param
import com.google.caliper.Runner
import com.google.caliper.SimpleBenchmark
import com.mikea.bayes.BayesianNetwork
import com.mikea.bayes.Var

/**
 * @author mike.aizatsky@gmail.com
 */
object LinearGraphBenchmark {
  def main(args: Array[String]) {
    Runner.main(classOf[LinearGraphBenchmark], args)
  }
}

class LinearGraphBenchmark extends SimpleBenchmark {
  protected override def setUp {
    ???
/*
    super.setUp
    val space: ProbabilitySpace = new ProbabilitySpace
    val vars: Array[Var] = new Array[Var](size)
    {
      var i: Int = 0
      while (i < size) {
        {
          vars(i) = space.newVar(String.valueOf(i), 2)
        }
        ({
          i += 1; i
        })
      }
    }
    var networkBuilder: Nothing = BayesianNetwork.withVariables(vars)
    {
      var i: Int = size - 1
      while (i > 0) {
        {
          networkBuilder = networkBuilder.edge(vars(i), vars(i - 1))
          val factor: Factor = Factor.withVariables(vars(i), vars(i - 1)).uniform(0.5).build
          networkBuilder = networkBuilder.factor(vars(i - 1), factor)
        }
        ({
          i -= 1; i
        })
      }
    }
    val lastVar: Var = vars(vars.length - 1)
    networkBuilder.factor(lastVar, Factor.withVariables(lastVar).uniform(0.5).build)
    network = networkBuilder.build
    queryVar = vars(0)
*/
  }

  @SuppressWarnings(Array("UnusedDeclaration")) def timeQuery(reps: Int) {
    ???
/*
    {
      var i: Int = 0
      while (i < reps) {
        {
          network.query(queryVar)
        }
        ({
          i += 1; i
        })
      }
    }
*/
  }

  @Param(Array("1", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100")) private[query] var size: Int = 0
  private var network: BayesianNetwork = null
  private var queryVar: Var = null
}