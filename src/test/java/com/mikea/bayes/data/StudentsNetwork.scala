package com.mikea.bayes.data

import com.mikea.bayes._
import org.junit.Assert.assertEquals

/**
 * @author mike.aizatsky@gmail.com
 */
object StudentsNetwork {
  def buildStudentsNetwork: BayesianNetwork = {
    val factorD: Factor = new Factor(VarSet(D), Array[Double](0.6, 0.4))
    val factorI: Factor = new Factor(VarSet(I), Array[Double](0.7, 0.3))
    val factorG: Factor = new Factor(VarSet(G, I, D), Array[Double](0.3, 0.05, 0.9, 0.5, 0.4, 0.25, 0.08, 0.3, 0.3, 0.7, 0.02, 0.2))
    val factorS: Factor = new Factor(VarSet(S, I), Array[Double](0.95, 0.2, 0.05, 0.8))
    val factorL: Factor = new Factor(VarSet(L, G), Array[Double](0.1, 0.4, 0.99, 0.9, 0.6, 0.01))
    assertEquals(0.3, factorG.getValue(VarAssignment.at(G, 0).at(D, 0).at(I, 0)), 1e-5)
    assertEquals(0.4, factorG.getValue(VarAssignment.at(G, 1).at(D, 0).at(I, 0)), 1e-5)
    assertEquals(0.3, factorG.getValue(VarAssignment.at(G, 2).at(D, 0).at(I, 0)), 1e-5)
    assertEquals(0.95, factorS.getValue(VarAssignment.at(S, 0).at(I, 0)), 1e-5)
    assertEquals(0.05, factorS.getValue(VarAssignment.at(S, 1).at(I, 0)), 1e-5)

    BayesianNetwork.withVariables(D, I, G, S, L).edge(D, G).edge(I, G).edge(I, S).edge(G, L).factor(D, factorD).factor(I, factorI).factor(G, factorG).factor(S, factorS).factor(L, factorL).build()
  }

  val space = new ProbabilitySpace("StudentsNetwork")
  val D = space.newVar("D", 2)
  val I = space.newVar("I", 2)
  val G = space.newVar("G", 3)
  val S = space.newVar("S", 2)
  val L = space.newVar("L", 2)
}