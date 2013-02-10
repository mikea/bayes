package com.mikea.bayes.benchmarks.query

import com.google.caliper.Param
import com.google.caliper.Runner
import com.google.caliper.SimpleBenchmark
import com.mikea.bayes.BayesianNetwork
import com.mikea.bayes.Var
import com.mikea.bayes.io.HuginNetFile
import com.mikea.bayes.query.QueryAlgorithm
import java.io.FileInputStream

/**
 * @author mike.aizatsky@gmail.com
 */
object QueryBenchmark {
  def main(args: Array[String]) {
    Runner.main(classOf[QueryBenchmark], args)
  }

  private abstract trait Test {
    def setUp

    def time1

    def time2

    def time3

    def time4
  }

  private class CysticFibrosisBayesNet extends Test {
    ???
/*
    def this(queryAlgorithm: QueryAlgorithm[_]) {
      this()
      this.queryAlgorithm = queryAlgorithm
    }
*/

    def setUp {
      network = HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNet.net"))
      jasonGenotype = network.getVarByName("JasonGenotype")
      jasonPhenotype = network.getVarByName("JasonPhenotype")
      evaPhenotype = network.getVarByName("EvaPhenotype")
      reneGenotype = network.getVarByName("ReneGenotype")
    }

    def time1 {
      ???
//      network.query(queryAlgorithm, jasonGenotype)
    }

    def time2 {
      ???
//      network.query(queryAlgorithm, jasonPhenotype)
    }

    def time3 {
      ???
//      network.query(queryAlgorithm, vars(jasonPhenotype), vars(evaPhenotype), strings("CysticFibrosis"))
    }

    def time4 {
      ???
//      network.query(queryAlgorithm, vars(jasonPhenotype), vars(evaPhenotype, reneGenotype), strings("CysticFibrosis", "FF"))
    }

    private final val queryAlgorithm: QueryAlgorithm[_] = null
    private var network: BayesianNetwork = null
    private var jasonGenotype: Var = null
    private var jasonPhenotype: Var = null
    private var reneGenotype: Var = null
    private var evaPhenotype: Var = null
  }

  private class CysticFibrosisBayesNetGeneCopy extends Test {
    ???
/*
    def this(queryAlgorithm: QueryAlgorithm[_]) {
      this()
      this.queryAlgorithm = queryAlgorithm
    }
*/

    def setUp {
      network = HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNetGeneCopy.net"))
      jasonPhenotype = network.getVarByName("JasonPhenotype")
      evaPhenotype = network.getVarByName("EvaPhenotype")
      renePhenotype = network.getVarByName("RenePhenotype")
      robinPhenotype = network.getVarByName("RobinPhenotype")
    }

    def time1 {
      ???
//      network.query(queryAlgorithm, robinPhenotype)
    }

    def time2 {
      ???
//      network.query(queryAlgorithm, jasonPhenotype)
    }

    def time3 {
      ???
//      network.query(queryAlgorithm, vars(jasonPhenotype), vars(evaPhenotype), strings("CysticFibrosis"))
    }

    def time4 {
      ???
//      network.query(queryAlgorithm, vars(jasonPhenotype), vars(evaPhenotype, renePhenotype), strings("CysticFibrosis", "NoCysticFibrosis"))
    }

    private final val queryAlgorithm: QueryAlgorithm[_] = null
    private var network: BayesianNetwork = null
    private var jasonPhenotype: Var = null
    private var robinPhenotype: Var = null
    private var renePhenotype: Var = null
    private var evaPhenotype: Var = null
  }

}

class QueryBenchmark extends SimpleBenchmark {
  protected override def setUp {
    ???
/*    var queryAlgorithm: QueryAlgorithm[_] = null
    if (algorithm == "VarElimination.MinNeighbors") {
      queryAlgorithm = new VarElimination(new SumProduct.MinNeighborsStrategy)
    }
    else if (algorithm == "VarElimination.MinWeight") {
      queryAlgorithm = new VarElimination(new SumProduct.MinWeightStrategy)
    }
    else if (algorithm == "VarElimination.MinFill") {
      queryAlgorithm = new VarElimination(new SumProduct.MinFillStrategy)
    }
    else {
      throw new IllegalArgumentException(algorithm)
    }
    if (network == "cysticFibrosisBayesNet") {
      test = new QueryBenchmark.CysticFibrosisBayesNet(queryAlgorithm)
    }
    else if (network == "cysticFibrosisBayesNetGeneCopy") {
      test = new QueryBenchmark.CysticFibrosisBayesNetGeneCopy(queryAlgorithm)
    }
    else {
      throw new IllegalArgumentException(network)
    }
    test.setUp*/
  }

  @SuppressWarnings(Array("UnusedDeclaration")) def time1(reps: Int) {
    {
      var i: Int = 0
      while (i < reps) {
        {
          test.time1
        }
        ({
          i += 1; i
        })
      }
    }
  }

  @SuppressWarnings(Array("UnusedDeclaration")) def time2(reps: Int) {
    {
      var i: Int = 0
      while (i < reps) {
        {
          test.time2
        }
        ({
          i += 1; i
        })
      }
    }
  }

  @SuppressWarnings(Array("UnusedDeclaration")) def time3(reps: Int) {
    {
      var i: Int = 0
      while (i < reps) {
        {
          test.time3
        }
        ({
          i += 1; i
        })
      }
    }
  }

  @SuppressWarnings(Array("UnusedDeclaration")) def time4(reps: Int) {
    {
      var i: Int = 0
      while (i < reps) {
        {
          test.time4
        }
        ({
          i += 1; i
        })
      }
    }
  }

  @Param(Array("cysticFibrosisBayesNet", "cysticFibrosisBayesNetGeneCopy")) private var network: String = null
  @Param(Array("VarElimination.MinNeighbors", "VarElimination.MinWeight", "VarElimination.MinFill")) private var algorithm: String = null
  private var test: QueryBenchmark.Test = null
}