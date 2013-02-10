package com.mikea.bayes

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import com.mikea.bayes.VarSet.{WithScanner, Scanner}

/**
 * @author mike.aizatsky@gmail.com
 */
class VarSetTest {
  val space = new ProbabilitySpace("VarSetTest")
  val var0 = space.newVar("0", 1)
  val var1 = space.newVar("1", 2)
  val var2 = space.newVar("2", 2)
  val var3 = space.newVar("3", 3)
  val var4 = space.newVar("4", 4)

  def assertGetIndex(v: VarSet) {
    for (i <- 0 until v.cardinality) {
      assertEquals(i, v.getIndex(v.getAssignment(i)))
    }
  }

  @Test def testToString {
    val v1: VarSet = VarSet(var1)
    val v2: VarSet = VarSet(var2, var1)
    val v3: VarSet = VarSet(var3, var2)
    assertEquals("{1}", v1.toString)
    assertEquals("{2, 1}", v2.toString)
    assertEquals("{3, 2}", v3.toString)
    assertEquals("{3(3), 2(2)}", v3.toString(true))
  }

  @Test def testHasVariable {
    val v= VarSet(var2, var1)
    assertTrue(v.contains(var1))
    assertTrue(v.contains(var2))
    assertTrue(!v.contains(var3))
  }

  @Test def testUnion {
    val v1: VarSet = VarSet(var1)
    val v2: VarSet = VarSet(var2, var1)
    val v3: VarSet = VarSet(var3, var2)
    assertEquals("{1, 2}", VarSet.union(List(v1, v2)).toString)
    assertEquals("{1, 2, 3}", VarSet.union(List(v1, v3)).toString)
    assertEquals("{1, 2, 3}", VarSet.union(List(v2, v3)).toString)
    assertEquals("{1, 2, 3}", VarSet.union(List(v1, v2, v3)).toString)
  }

  @Test def testGetIndex() {
    var v = VarSet(var1)
    assertGetIndex(v)
    v = VarSet(var1)
    assertGetIndex(v)
    v = VarSet(var0, var3)
    assertGetIndex(v)
  }

  @Test def testAssignmentOrder() {
    assertEquals("{1=0}\n" + "{1=1}", getAssignmentOrder(VarSet(var1)))
    assertEquals("{2=0, 3=0}\n" + "{2=0, 3=1}\n" + "{2=0, 3=2}\n" + "{2=1, 3=0}\n" + "{2=1, 3=1}\n" + "{2=1, 3=2}", getAssignmentOrder(VarSet(var2, var3)))
    assertEquals("{2=0, 3=0, 4=0}\n" + "{2=0, 3=0, 4=1}\n" + "{2=0, 3=0, 4=2}\n" + "{2=0, 3=0, 4=3}\n" + "{2=0, 3=1, 4=0}\n" + "{2=0, 3=1, 4=1}\n" + "{2=0, 3=1, 4=2}\n" + "{2=0, 3=1, 4=3}\n" + "{2=0, 3=2, 4=0}\n" + "{2=0, 3=2, 4=1}\n" + "{2=0, 3=2, 4=2}\n" + "{2=0, 3=2, 4=3}\n" + "{2=1, 3=0, 4=0}\n" + "{2=1, 3=0, 4=1}\n" + "{2=1, 3=0, 4=2}\n" + "{2=1, 3=0, 4=3}\n" + "{2=1, 3=1, 4=0}\n" + "{2=1, 3=1, 4=1}\n" + "{2=1, 3=1, 4=2}\n" + "{2=1, 3=1, 4=3}\n" + "{2=1, 3=2, 4=0}\n" + "{2=1, 3=2, 4=1}\n" + "{2=1, 3=2, 4=2}\n" + "{2=1, 3=2, 4=3}", getAssignmentOrder(VarSet(var2, var3, var4)))
  }

  @Test def testRemoveVars() {
    val v1: VarSet = VarSet(var1, var2)
    val v2: VarSet = VarSet(var2)
    assertEquals("{1}", v1.removeVars(v2).toString)
  }

  @Test def testScan() {
    assertEquals(" 0 : {1=0}\n" + " 1 : {1=1}", scan(VarSet(var1)))
    assertEquals(" 0 : {2=0, 3=0}\n" + " 1 : {2=0, 3=1}\n" + " 2 : {2=0, 3=2}\n" + " 3 : {2=1, 3=0}\n" + " 4 : {2=1, 3=1}\n" + " 5 : {2=1, 3=2}", scan(VarSet(var2, var3)))
    assertEquals(" 0 : {1=0, 2=0, 3=0}\n" + " 1 : {1=0, 2=0, 3=1}\n" + " 2 : {1=0, 2=0, 3=2}\n" + " 3 : {1=0, 2=1, 3=0}\n" + " 4 : {1=0, 2=1, 3=1}\n" + " 5 : {1=0, 2=1, 3=2}\n" + " 6 : {1=1, 2=0, 3=0}\n" + " 7 : {1=1, 2=0, 3=1}\n" + " 8 : {1=1, 2=0, 3=2}\n" + " 9 : {1=1, 2=1, 3=0}\n" + "10 : {1=1, 2=1, 3=1}\n" + "11 : {1=1, 2=1, 3=2}", scan(VarSet(var1, var2, var3)))
  }

  @Test def testScanWith() {
    assertEquals(" 0 : {1=0} -  0 : {1=0}\n" + " 1 : {1=1} -  1 : {1=1}", scanWith(VarSet(var1), VarSet(var1)))
    assertEquals(" 0 : {1=0, 2=0} -  0 : {1=0}\n" + " 1 : {1=0, 2=1} -  0 : {1=0}\n" + " 2 : {1=1, 2=0} -  1 : {1=1}\n" + " 3 : {1=1, 2=1} -  1 : {1=1}", scanWith(VarSet(var1, var2), VarSet(var1)))
    assertEquals(" 0 : {1=0, 2=0, 3=0} -  0 : {1=0, 2=0}\n" + " 1 : {1=0, 2=0, 3=1} -  0 : {1=0, 2=0}\n" + " 2 : {1=0, 2=0, 3=2} -  0 : {1=0, 2=0}\n" + " 3 : {1=0, 2=1, 3=0} -  1 : {1=0, 2=1}\n" + " 4 : {1=0, 2=1, 3=1} -  1 : {1=0, 2=1}\n" + " 5 : {1=0, 2=1, 3=2} -  1 : {1=0, 2=1}\n" + " 6 : {1=1, 2=0, 3=0} -  2 : {1=1, 2=0}\n" + " 7 : {1=1, 2=0, 3=1} -  2 : {1=1, 2=0}\n" + " 8 : {1=1, 2=0, 3=2} -  2 : {1=1, 2=0}\n" + " 9 : {1=1, 2=1, 3=0} -  3 : {1=1, 2=1}\n" + "10 : {1=1, 2=1, 3=1} -  3 : {1=1, 2=1}\n" + "11 : {1=1, 2=1, 3=2} -  3 : {1=1, 2=1}", scanWith(VarSet(var1, var2, var3), VarSet(var1, var2)))
  }

  private def getAssignmentOrder(v: VarSet): String = {
    v.assignments().map(a => a.toString()).mkString("\n")
  }

  private def scan(v: VarSet): String = {
    var order: Vector[String] = Vector()
    v.scan(new Scanner {
      def scan(index: Int, varAssignment: VarAssignment) {
        order :+= f"$index%2d : $varAssignment"
      }
    })
    order.sorted.mkString("\n")
  }

  private def scanWith(v1: VarSet, v2: VarSet): String = {
    var order: Vector[String] = Vector()
    v1.scanWith(v2, new WithScanner {
      def scan(index1: Int, index2: Int) {
        val v1Assignment: VarAssignment = v1.getAssignment(index1)
        val v2Assignment: VarAssignment = v2.getAssignment(index2)
        order :+= f"$index1%2d : $v1Assignment - $index2%2d : $v2Assignment"
      }
    })
    order.sorted.mkString("\n")
  }

}