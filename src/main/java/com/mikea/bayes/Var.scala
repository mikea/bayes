package com.mikea.bayes

import java.util.NoSuchElementException
import beans.BeanProperty

object Var {

/*
  val BY_NAME = new Ordering[Var]() {

    override def compare(left: Var, right: Var): Int = {
      assert left != null
      assert right != null
      left.getName.compareTo(right.getName)
    }
  }
*/

/*
  def vars(vv: Var*): Array[Var] = vv
*/
}

/**
 * @author mike.aizatsky@gmail.com
 */
class Var private[bayes] (val space: ProbabilitySpace,
                          @BeanProperty val index: Int,
                          @BeanProperty val name: String,
                          val card: Int,
                          val stateNames: Array[String]) {
  def cardinality: Int = card

  override def toString(): String = toString(false)

  def toString(showCardinality: Boolean): String = {
    if (!showCardinality) name else {
      val result = new StringBuilder()
      result.append(name)
      result.append("(")
      result.append(card)
      if (stateNames != null) {
        result.append(", ")
        result.append(stateNames)
      }
      result.append(")")
      result.toString
    }
  }

  def getValue(value: Int): String = {
    if (stateNames == null) {
      String.valueOf(value)
    } else {
      stateNames(value)
    }
  }

  def getValueIndex(value: String): Int = {
    if (stateNames == null) {
      Integer.parseInt(value)
    } else {
      for (i <- 0 until stateNames.length if stateNames(i) == value) return i
      throw new NoSuchElementException()
    }
  }

  def probabilitySpace: ProbabilitySpace = space
}
