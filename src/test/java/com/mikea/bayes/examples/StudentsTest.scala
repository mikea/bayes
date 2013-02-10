
package com.mikea.bayes.examples

import com.mikea.bayes.BayesianNetwork
import org.junit.Test
import com.mikea.bayes.VarAssignment.at
import com.mikea.bayes.data.StudentsNetwork.D
import com.mikea.bayes.data.StudentsNetwork.G
import com.mikea.bayes.data.StudentsNetwork.I
import com.mikea.bayes.data.StudentsNetwork.L
import com.mikea.bayes.data.StudentsNetwork.S
import com.mikea.bayes.data.StudentsNetwork.buildStudentsNetwork
import org.junit.Assert.assertEquals

/**
 * Applying library to Students example from Koller.
 *
 * @author mike.aizatsky@gmail.com
 */
object StudentsTest {
  private def ints(values: Int*): Array[Int] = {
    ???
//    return values
  }
}

class StudentsTest {
  @Test def testValidate {
    val n: BayesianNetwork = buildStudentsNetwork
    n.validate
  }

  @Test def testComputeProbability {
    val n: BayesianNetwork = buildStudentsNetwork
    assertEquals(0.004608, n.computeProbability(at(D, 0).at(I, 1).at(G, 1).at(S, 1).at(L, 0)), 1e-5)
  }

  @Test def testJointDistribution {
    ???
/*
    val n: BayesianNetwork = buildStudentsNetwork
    val f: Factor = n.computeJointDistribution
    assertEquals(0.004608, f.getValue(vars(D, I, G, S, L), Array[Int](0, 1, 1, 1, 0)), 1e-5)
    assertEquals("Factor({L}, [0.4977, 0.5023])", n.query(L).toString("%.4f"))
    assertEquals("Factor({L}, [0.6114, 0.3886])", n.query(newVarSet(L), new VarAssignment(vars(I), ints(0))).toString("%.4f"))
    assertEquals("Factor({L}, [0.4870, 0.5130])", n.query(newVarSet(L), new VarAssignment(vars(I, D), ints(0, 0))).toString("%.4f"))
    assertEquals("Factor({I}, [0.7000, 0.3000])", n.query(I).toString("%.4f"))
    assertEquals("Factor({I}, [0.9211, 0.0789])", n.query(newVarSet(I), new VarAssignment(vars(G), Array[Int](2))).toString("%.4f"))
    assertEquals("Factor({D}, [0.3707, 0.6293])", n.query(newVarSet(D), new VarAssignment(vars(G), Array[Int](2))).toString("%.4f"))
    assertEquals("Factor({I}, [0.8600, 0.1400])", n.query(newVarSet(I), new VarAssignment(vars(L), Array[Int](0))).toString("%.4f"))
    assertEquals("Factor({I}, [0.9211, 0.0789])", n.query(newVarSet(I), new VarAssignment(vars(G, L), Array[Int](2, 0))).toString("%.4f"))
    assertEquals("Factor({I}, [0.4217, 0.5783])", n.query(newVarSet(I), new VarAssignment(vars(G, S), Array[Int](2, 1))).toString("%.4f"))
*/
  }
}