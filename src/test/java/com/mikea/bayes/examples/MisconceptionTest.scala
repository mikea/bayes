package com.mikea.bayes.examples

import org.junit.Test

/**
 * Misconception example from Koller.
 *
 * @author mike.aizatsky@gmail.com
 */
class MisconceptionTest {
  @Test def testMisconceptionFactors {
    ???
/*
    val space: ProbabilitySpace = new ProbabilitySpace
    val a: Var = space.newVar("A", 2)
    val b: Var = space.newVar("B", 2)
    val c: Var = space.newVar("C", 2)
    val d: Var = space.newVar("D", 2)
    val phi1: Factor = Factor.withVariables(a, b).row(a, at(b, 0), Array[Double](30, 1)).row(a, at(b, 1), Array[Double](5, 10)).build
    val phi2: Factor = Factor.withVariables(b, c).row(b, at(c, 0), Array[Double](100, 1)).row(b, at(c, 1), Array[Double](1, 100)).build
    val phi3: Factor = Factor.withVariables(c, d).row(c, at(d, 0), Array[Double](1, 100)).row(c, at(d, 1), Array[Double](100, 1)).build
    val phi4: Factor = Factor.withVariables(d, a).row(d, at(a, 0), Array[Double](100, 1)).row(d, at(a, 1), Array[Double](1, 100)).build
    val unnormalizedMeasure: Factor = Factor.product(phi1, phi2, phi3, phi4)
    assertEquals("Factor({A, B, C, D}):\n" + "{A=0, B=0, C=0, D=0}:    300,000.0\n" + "{A=0, B=0, C=0, D=1}:    300,000.0\n" + "{A=0, B=0, C=1, D=0}:    300,000.0\n" + "{A=0, B=0, C=1, D=1}:         30.0\n" + "{A=0, B=1, C=0, D=0}:        500.0\n" + "{A=0, B=1, C=0, D=1}:        500.0\n" + "{A=0, B=1, C=1, D=0}:  5,000,000.0\n" + "{A=0, B=1, C=1, D=1}:        500.0\n" + "{A=1, B=0, C=0, D=0}:        100.0\n" + "{A=1, B=0, C=0, D=1}:  1,000,000.0\n" + "{A=1, B=0, C=1, D=0}:        100.0\n" + "{A=1, B=0, C=1, D=1}:        100.0\n" + "{A=1, B=1, C=0, D=0}:         10.0\n" + "{A=1, B=1, C=0, D=1}:    100,000.0\n" + "{A=1, B=1, C=1, D=0}:    100,000.0\n" + "{A=1, B=1, C=1, D=1}:    100,000.0\n", unnormalizedMeasure.toStringAsTable("%,12.1f"))
*/
  }
}