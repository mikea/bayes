package com.mikea.bayes.sampling

import org.junit.Test

/**
 * @author mike.aizatsky@gmail.com
 */
class ForwardSamplerTest {
  @Test def testProbabilities {
    ???
/*
    val network: BayesianNetwork = StudentsNetwork.buildStudentsNetwork
    val nSamples: Int = 10000
    val queryResult: Nothing = QueryAlgorithm.VAR_ELIMINATION.prepare(network)
    val samples: Array[VarAssignment] = ForwardSampler.sample(network, nSamples)
    val counts: Array[Array[Int]] = new Array[Array[Int]](network.V)
    for (sample <- samples) {
      import scala.collection.JavaConversions._
      for (`var` <- sample) {
        val v: Int = `var`.getIndex
        if (counts(v) == null) counts(v) = new Array[Int](`var`.cardinality)
        counts(v)(sample.get(`var`)) += 1
      }
    }
    {
      var i: Int = 0
      while (i < network.V) {
        {
          val `var`: Var = network.getVar(i)
          var sum: Int = 0
          val varCounts: Array[Int] = counts(`var`.getIndex)
          for (varCount <- varCounts) {
            sum += varCount
          }
          assertEquals(nSamples, sum)
          {
            var j: Int = 0
            while (j < varCounts.length) {
              {
                val p: Double = varCounts(j).asInstanceOf[Double] / sum
                val expectedProb: Double = queryResult.getProbability(new VarAssignment(Array[Var](`var`), Array[Int](j)))
                assertEquals(expectedProb, p, 1e-2)
              }
              ({
                j += 1; j - 1
              })
            }
          }
        }
        ({
          i += 1; i
        })
      }
    }
*/
  }
}