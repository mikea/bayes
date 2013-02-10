package com.mikea.bayes

import com.mikea.bayes.query.QueryAlgorithm
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Collection

/**
 * @author mike.aizatsky@gmail.com
 */
@RunWith(classOf[Parameterized]) object QueryTest {
}

@RunWith(classOf[Parameterized]) class QueryTest(algorithm: QueryAlgorithm[_]) {
  @Parameterized.Parameters def data: Collection[Array[AnyRef]] = ???/*{
    return Arrays.asList(Array[Array[AnyRef]](Array(new VarElimination(new SumProduct.MinNeighborsStrategy)), Array(new VarElimination(new SumProduct.MinWeightStrategy))))
  }*/

  @Test def testQueryLinear {
    ???
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val size: Int = 10
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
          val factor: Factor = Factor.withVariables(vars(i), vars(i - 1)).uniform(0.25).build
          networkBuilder = networkBuilder.factor(vars(i - 1), factor)
        }
        ({
          i -= 1; i
        })
      }
    }
    val lastVar: Var = vars(vars.length - 1)
    networkBuilder.factor(lastVar, Factor.withVariables(lastVar).uniform(0.5).build)
    val network: BayesianNetwork = networkBuilder.build
    assertEquals("Factor({0}, [0.5, 0.5])", network.query(algorithm, vars(0)).toString)
    assertEquals("Factor({0, 1}, [0.25, 0.25, 0.25, 0.25])", network.query(algorithm, vars(0), vars(1)).toString)
    assertEquals("Factor({0}, [0.5, 0.5])", network.query(algorithm, newVarSet(vars(0)), new VarAssignment(Array[Var](vars(1)), Array[Int](0))).toString)
*/
  }

  @Test def testCysticFibrosisBayesNet {
    ???
/*
    val network: BayesianNetwork = HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNet.net"))
    val jasonGenotype: Var = network.getVarByName("JasonGenotype")
    val jasonPhenotype: Var = network.getVarByName("JasonPhenotype")
    val evaPhenotype: Var = network.getVarByName("EvaPhenotype")
    val reneGenotype: Var = network.getVarByName("ReneGenotype")
    assertEquals("Factor({JasonGenotype}, [0.0100, 0.1800, 0.8100])", network.query(algorithm, jasonGenotype).toString("%.4f"))
    assertEquals("Factor({JasonPhenotype}, [0.1970, 0.8030])", network.query(algorithm, jasonPhenotype).toString("%.4f"))
    assertEquals("Factor({JasonPhenotype}, [0.2475, 0.7525])", network.query(algorithm, vars(jasonPhenotype), vars(evaPhenotype), strings("CysticFibrosis")).toString("%.4f"))
    assertEquals("Factor({JasonPhenotype}, [0.6415, 0.3585])", network.query(algorithm, vars(jasonPhenotype), vars(evaPhenotype, reneGenotype), strings("CysticFibrosis", "FF")).toString("%.4f"))
*/
  }
}