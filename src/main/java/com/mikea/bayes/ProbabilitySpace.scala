package com.mikea.bayes

import scala.collection.mutable.ArrayBuffer
import com.google.common.base.Objects

object ProbabilitySpace {

  def fromVars(vars: Iterable[Var]): ProbabilitySpace = {
    var space: ProbabilitySpace = null
    for (v <- vars) {
      if (space == null) {
        space = v.probabilitySpace
      } else {
        assert(space == `v`.probabilitySpace)
      }
    }
    assert(space != null)
    space
  }

  def fromVarSets(varSets: Iterable[VarSet]): ProbabilitySpace = {
    var space: ProbabilitySpace = null
    for (varSet <- varSets) {
      if (space == null) {
        space = fromVars(varSet)
      } else {
        assert(space == fromVars(varSet))
      }
    }
    assert(space != null)
    space
  }
}

/**
 * @author mike.aizatsky@gmail.com
 */
class ProbabilitySpace(val name: String) {

  private var nextVarIndex: Int = 0

  private var mutable: Boolean = true

  private val vars = ArrayBuffer.empty[Var]

  def this() {
    this("Anonymous")
  }

  def newVar(name: String, card: Int): Var = newVar(name, card, null)

  def newVar(name: String, card: Int, stateNames: Array[String]): Var = {
    synchronized {
      require(mutable)

      val idx = nextVarIndex
      nextVarIndex += 1
      val v = new Var(this, idx, name, card, stateNames)
      vars += v
      assert(vars.size == nextVarIndex)
      v
    }
  }

  def numVars: Int = {
    synchronized {
      mutable = false
      nextVarIndex
    }
  }

  def getVar(index: Int): Var = vars(index)

  def variables: Iterable[Var] = vars

  override def toString(): String = {
    Objects.toStringHelper(this).add("name", name).toString
  }
}
