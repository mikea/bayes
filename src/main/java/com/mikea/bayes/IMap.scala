package com.mikea.bayes

import com.mikea.bayes.IMap.Independence
import com.google.common.base.Objects


object IMap {

  def computeIMap(network: BayesianNetwork): IMap = {
    val vars = network.varSet
    val allPossibleObservations = vars.subsets
    var independences: Set[Independence] = Set()
    for (observation <- allPossibleObservations) {
      val varSet = VarSet.newVarSet(observation)
      val dSeparatedPairs = DSeparation.findAllDSeparatedPairs(network, varSet)
      for (pair <- dSeparatedPairs) {
        independences += new Independence(pair._1, pair._2, varSet)
      }
    }
    new IMap(independences)
  }

  class Independence(val var1: Var, val var2: Var, val observation: VarSet) {
    override def toString(): String = {
      "(" + var1.getName + " ⊥ " + var2.getName + " | " + observation.toString(false) + ")"
    }

    override def equals(that: Any): Boolean = {
      that match {
        case i: Independence => observation == i.observation && var1 == i.var1 && var2 == i.var2
        case _ => false
      }
    }

    override def hashCode(): Int = {
      Objects.hashCode(var1, var2, observation)
    }
  }
}

/**
 * @author mike.aizatsky@gmail.com
 */
class IMap private (val independencies: Set[Independence]) {

  override def toString(): String = {
    val strings = independencies map (i => i.toString)
    strings.toArray.sorted.toString
  }

  def contains(that: IMap): Boolean = {
    that.independencies.subsetOf(independencies)
  }

  override def equals(o: Any): Boolean = {
    if (this == o) return true
    if (o == null || getClass != o.getClass) return false
    val that = o.asInstanceOf[IMap]
    Objects.equal(independencies, that.independencies)
  }

  override def hashCode(): Int = Objects hashCode independencies
}
