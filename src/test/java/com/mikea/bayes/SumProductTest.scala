package com.mikea.bayes

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Collection

/**
 * @author mike.aizatsky@gmail.com
 */
@RunWith(classOf[Parameterized]) object SumProductTest {
}

@RunWith(classOf[Parameterized]) class SumProductTest(strategy: SumProduct.VarOrderStrategy) {
  @Parameterized.Parameters(name = "{index}: {0}") def parameters: Collection[Array[AnyRef]] = ???/*{
    return asList(Array[Array[AnyRef]](Array(new SumProduct.MinNeighborsStrategy), Array(new SumProduct.MinWeightStrategy), Array(new SumProduct.MinFillStrategy)))
  }*/

  private final val space: ProbabilitySpace = new ProbabilitySpace("SumProductTest")
  private final val var1: Var = space.newVar("1", 2)
  private final val var2: Var = space.newVar("2", 2)
  private final val var3: Var = space.newVar("3", 2)

  @Test def testSumProduct {
    ???
/*
    val f1: Factor = newFactor(vars(var1), Array[Double](0.11, 0.89))
    val f2: Factor = newFactor(vars(var1, var2), Array[Double](0.59, 0.41, 0.22, 0.78))
    val f3: Factor = newFactor(vars(var2, var3), Array[Double](0.39, 0.61, 0.06, 0.94))
    assertEquals("Factor({}, [1.0])", sumProductVariableElimination(newArrayList(var1, var2, var3), newArrayList(f1, f2, f3), strategy).toString)
*/
  }
}