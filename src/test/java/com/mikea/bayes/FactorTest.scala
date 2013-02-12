package com.mikea.bayes

import com.mikea.bayes.data.StudentsNetwork
import org.gga.graph.maps.DataGraph
import org.junit.Test
import com.mikea.bayes.Factor.newFactor
import org.junit.Assert.assertEquals

class FactorTest {
  val space: ProbabilitySpace = new ProbabilitySpace("VarSetTest")
  val var1: Var = space.newVar("1", 2)
  val var2: Var = space.newVar("2", 2)
  val var3: Var = space.newVar("3", 2)

  @Test def testToString() {
    val f1: Factor = newFactor(VarSet(var1), Array[Double](0.11, 0.89))
    val f2: Factor = newFactor(VarSet(var2, var1), Array[Double](0.59, 0.41, 0.22, 0.78))
    assertEquals("Factor({1}, [0.11, 0.89])", f1.toString)
    assertEquals("Factor({2, 1}, [0.59, 0.41, 0.22, 0.78])", f2.toString)
  }

  @Test def testProduct() {
    val f1: Factor = newFactor(VarSet(var1), Array[Double](0.11, 0.89))
    val f2: Factor = newFactor(VarSet(var1, var2), Array[Double](0.59, 0.41, 0.22, 0.78))
    val f3: Factor = newFactor(VarSet(var2, var3), Array[Double](0.39, 0.61, 0.06, 0.94))
    assertEquals("Factor({1, 2}, [0.0649, 0.045099999999999994, 0.1958, 0.6942])", f1.product(f2).toString)
    assertEquals("Factor({1, 2}, [0.0649, 0.045099999999999994, 0.1958, 0.6942])", f2.product(f1).toString)
    assertEquals("Factor({1, 2, 3}):\n" + "{1=0, 2=0, 3=0}: 0.0429\n" + "{1=0, 2=0, 3=1}: 0.0671\n" + "{1=0, 2=1, 3=0}: 0.0066\n" + "{1=0, 2=1, 3=1}: 0.1034\n" + "{1=1, 2=0, 3=0}: 0.3471\n" + "{1=1, 2=0, 3=1}: 0.5429\n" + "{1=1, 2=1, 3=0}: 0.0534\n" + "{1=1, 2=1, 3=1}: 0.8366\n", Factor.product(List(f1, f3)).toStringAsTable("%.4f"))
    assertEquals("Factor({1, 2, 3}):\n" + "{1=0, 2=0, 3=0}: 0.0429\n" + "{1=0, 2=0, 3=1}: 0.0671\n" + "{1=0, 2=1, 3=0}: 0.0066\n" + "{1=0, 2=1, 3=1}: 0.1034\n" + "{1=1, 2=0, 3=0}: 0.3471\n" + "{1=1, 2=0, 3=1}: 0.5429\n" + "{1=1, 2=1, 3=0}: 0.0534\n" + "{1=1, 2=1, 3=1}: 0.8366\n", Factor.product(List(f3, f1)).toStringAsTable("%.4f"))
    assertEquals("Factor({1, 2, 3}):\n" + "{1=0, 2=0, 3=0}: 0.2301\n" + "{1=0, 2=0, 3=1}: 0.3599\n" + "{1=0, 2=1, 3=0}: 0.0246\n" + "{1=0, 2=1, 3=1}: 0.3854\n" + "{1=1, 2=0, 3=0}: 0.0858\n" + "{1=1, 2=0, 3=1}: 0.1342\n" + "{1=1, 2=1, 3=0}: 0.0468\n" + "{1=1, 2=1, 3=1}: 0.7332\n", Factor.product(List(f2, f3)).toStringAsTable("%.4f"))
    assertEquals("Factor({1, 2, 3}):\n" + "{1=0, 2=0, 3=0}: 0.2301\n" + "{1=0, 2=0, 3=1}: 0.3599\n" + "{1=0, 2=1, 3=0}: 0.0246\n" + "{1=0, 2=1, 3=1}: 0.3854\n" + "{1=1, 2=0, 3=0}: 0.0858\n" + "{1=1, 2=0, 3=1}: 0.1342\n" + "{1=1, 2=1, 3=0}: 0.0468\n" + "{1=1, 2=1, 3=1}: 0.7332\n", Factor.product(List(f3, f2)).toStringAsTable("%.4f"))
    assertEquals("Factor({1, 2, 3}):\n" + "{1=0, 2=0, 3=0}: 0.0253\n" + "{1=0, 2=0, 3=1}: 0.0396\n" + "{1=0, 2=1, 3=0}: 0.0027\n" + "{1=0, 2=1, 3=1}: 0.0424\n" + "{1=1, 2=0, 3=0}: 0.0764\n" + "{1=1, 2=0, 3=1}: 0.1194\n" + "{1=1, 2=1, 3=0}: 0.0417\n" + "{1=1, 2=1, 3=1}: 0.6525\n", Factor.product(List(f1, f2, f3)).toStringAsTable("%.4f"))
    assertEquals("Factor({1, 2, 3}):\n" + "{1=0, 2=0, 3=0}: 0.0253\n" + "{1=0, 2=0, 3=1}: 0.0396\n" + "{1=0, 2=1, 3=0}: 0.0027\n" + "{1=0, 2=1, 3=1}: 0.0424\n" + "{1=1, 2=0, 3=0}: 0.0764\n" + "{1=1, 2=0, 3=1}: 0.1194\n" + "{1=1, 2=1, 3=0}: 0.0417\n" + "{1=1, 2=1, 3=1}: 0.6525\n", Factor.product(List(f1, f3, f2)).toStringAsTable("%.4f"))
    assertEquals("Factor({1, 2, 3}):\n" + "{1=0, 2=0, 3=0}: 0.0253\n" + "{1=0, 2=0, 3=1}: 0.0396\n" + "{1=0, 2=1, 3=0}: 0.0027\n" + "{1=0, 2=1, 3=1}: 0.0424\n" + "{1=1, 2=0, 3=0}: 0.0764\n" + "{1=1, 2=0, 3=1}: 0.1194\n" + "{1=1, 2=1, 3=0}: 0.0417\n" + "{1=1, 2=1, 3=1}: 0.6525\n", Factor.product(List(f3, f1, f2)).toStringAsTable("%.4f"))
  }

  @Test def testSum() {
    val f1: Factor = newFactor(VarSet(var1), Array[Double](0.11, 0.89))
    assertEquals(1.0, f1.sum(), 1e-6)
  }

  @Test def testNormalize() {
    var f1: Factor = newFactor(VarSet(var1), Array[Double](0.11 * 2, 0.89 * 2))
    f1 = f1.normalize()
    assertEquals("Factor({1}, [0.11, 0.89])", f1.toString)
  }

  @Test def testMarginalize() {
    val f2: Factor = newFactor(VarSet(var1, var2), Array[Double](0.59, 0.41, 0.22, 0.78))
    val f: Factor = f2.marginalize(VarSet(var2))
    assertEquals("Factor({1}, [1.0, 1.0])", f.toString)
  }

  @Test def testObserveEvidence() {
    val f1: Factor = newFactor(VarSet(var1), Array[Double](0.11, 0.89))
    assertEquals("Factor({1}):\n" + "{1=0}: 0.11\n" + "{1=1}: 0.89\n", f1.observeEvidence(List(), Array[Int]()).toStringAsTable("%.2f"))
    assertEquals("Factor({1}):\n" + "{1=0}: 0.11\n" + "{1=1}: 0.00\n", f1.observeEvidence(List(var1), Array(0)).toStringAsTable("%.2f"))
    assertEquals("Factor({1}):\n" + "{1=0}: 0.00\n" + "{1=1}: 0.89\n", f1.observeEvidence(List(var1), Array(1)).toStringAsTable("%.2f"))
    assertEquals("Factor({1}):\n" + "{1=0}: 0.11\n" + "{1=1}: 0.89\n", f1.observeEvidence(List(var2), Array(1)).toStringAsTable("%.2f"))
    val f2: Factor = newFactor(VarSet(var1, var2), Array[Double](0.59, 0.41, 0.22, 0.78))
    assertEquals("Factor({1, 2}):\n" + "{1=0, 2=0}: 0.59\n" + "{1=0, 2=1}: 0.00\n" + "{1=1, 2=0}: 0.22\n" + "{1=1, 2=1}: 0.00\n", f2.observeEvidence(List(var2), Array[Int](0)).toStringAsTable("%.2f"))
    assertEquals("Factor({1, 2}):\n" + "{1=0, 2=0}: 0.00\n" + "{1=0, 2=1}: 0.41\n" + "{1=1, 2=0}: 0.00\n" + "{1=1, 2=1}: 0.78\n", f2.observeEvidence(List(var2), Array[Int](1)).toStringAsTable("%.2f"))
    assertEquals("Factor({1, 2}):\n" + "{1=0, 2=0}: 0.59\n" + "{1=0, 2=1}: 0.41\n" + "{1=1, 2=0}: 0.00\n" + "{1=1, 2=1}: 0.00\n", f2.observeEvidence(List(var1), Array[Int](0)).toStringAsTable("%.2f"))
    assertEquals("Factor({1, 2}):\n" + "{1=0, 2=0}: 0.00\n" + "{1=0, 2=1}: 0.00\n" + "{1=1, 2=0}: 0.22\n" + "{1=1, 2=1}: 0.78\n", f2.observeEvidence(List(var1), Array[Int](1)).toStringAsTable("%.2f"))
    assertEquals("Factor({1, 2}):\n" + "{1=0, 2=0}: 0.00\n" + "{1=0, 2=1}: 0.00\n" + "{1=1, 2=0}: 0.22\n" + "{1=1, 2=1}: 0.00\n", f2.observeEvidence(List(var1, var2), Array[Int](1, 0)).toStringAsTable("%.2f"))
  }

  @Test def testInducedMarkovNetwork() {
    val factors: Array[Factor] = StudentsNetwork.buildStudentsNetwork.factors
    val network: DataGraph[Var, List[Factor]] = Factor.induceMarkovNetwork(factors)
    assertEquals(
      "DataGraphImpl{isDirected=false, [D, G, I, L, S], [\n" +
      "    D<->G:List(Factor({G, I, D}, [0.3, 0.05, 0.9, 0.5, 0.4, 0.25, 0.08, 0.3, 0.3, 0.7, 0.02, 0.2]))\n" +
      "    D<->I:List(Factor({G, I, D}, [0.3, 0.05, 0.9, 0.5, 0.4, 0.25, 0.08, 0.3, 0.3, 0.7, 0.02, 0.2]))\n" +
      "    G<->I:List(Factor({G, I, D}, [0.3, 0.05, 0.9, 0.5, 0.4, 0.25, 0.08, 0.3, 0.3, 0.7, 0.02, 0.2]))\n" +
      "    G<->L:List(Factor({L, G}, [0.1, 0.4, 0.99, 0.9, 0.6, 0.01]))\n" +
      "    I<->S:List(Factor({S, I}, [0.95, 0.2, 0.05, 0.8]))\n" +
        "]}",
      network.toString)
  }
}