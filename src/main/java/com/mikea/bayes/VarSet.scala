package com.mikea.bayes

import com.google.common.base.Objects
import com.google.common.base.Preconditions
import com.mikea.bayes.VarSet.{WithScanner, Scanner}

object VarSet {
  def union(varSets: Iterable[VarSet]): VarSet = {
    assert(!varSets.isEmpty)
    newVarSet(varSets.flatten.toSet)
  }

  def newVarSet(vars: Set[Var]): VarSet = new VarSet(vars.toList.sortBy(v => v.getName))
  def newVarSet(vars: Iterable[Var]): VarSet = new VarSet(vars.toList)
  def apply(vars: Var*): VarSet = new VarSet(vars)

  def intersect(varSets: Iterable[VarSet]): VarSet = {
    val v : Set[Var] = varSets.foldLeft(Set.empty[Var])((vv, ss) => vv & ss.set)
    newVarSet(v.toList)
  }

  trait Scanner {
    def scan(index: Int, varAssignment: VarAssignment)
  }

  trait WithScanner {
    def scan(index1: Int, index2: Int)
  }
}

/**
 * @author mike.aizatsky@gmail.com
 */
class VarSet private (val vars: Seq[Var]) extends Iterable[Var] {
  private val set = vars.toSet

  def add(vars: Iterable[Var]): VarSet = new VarSet(this.vars ++ vars)

  def add(varSet: VarSet): VarSet = new VarSet(this.vars ++ varSet.vars)

  override def toString = toString(showCardinalities = false)

  def toString(showCardinalities: Boolean): String = {
    val result = new StringBuilder("{")
    result.append(vars.map(v => v.toString(showCardinalities)).mkString(", "))
    result.append('}')
    result.toString()
  }

  def contains(v: Var): Boolean = set.contains(v)

  def find(v: Var): Int = vars.indexOf(v)

  def cardinality: Int = {
    val result: Long = vars.foldLeft(1l)((a, v) => a * v.cardinality)
    assert(result < Integer.MAX_VALUE, s"Factor too big: $result $this")
    result.toInt
  }

  def getIndex(assignment: VarAssignment): Int = {
    assert(assignment.containsAll(vars), s"Assignment $assignment does not match $this")
    var index = 0
    for (v <- vars) {
      val cardinality = v.cardinality
      val i = assignment.get(v)
      assert(i >= 0, s"Bad assignment $i@$v for set $this (full assignment: $assignment)")
      index *= cardinality
      index += i
    }
    index
  }

  def scan(scanner: Scanner) {
    val values = new Array[Int](vars.length)
    val cardinalities = new Array[Int](vars.length)
    for (i <- 0 until vars.length) {
      cardinalities(i) = vars(i).cardinality
    }
    var idx = 0
    val card = cardinality
    while (true) {
      scanner.scan(idx, new VarAssignment(vars, values))
      idx += 1
      if (idx == card) {
        return
      }
      var i = vars.length - 1
      values(i) += 1
      while (values(i) == cardinalities(i)) {
        values(i - 1) += 1
        values(i) = 0
        i -= 1
      }
    }
  }

  def scanWith(varSet2: VarSet, scanner: WithScanner) {
    Preconditions.checkArgument(containsAll(varSet2))
    val varMapping = new Array[Int](varSet2.vars.length)
    val cardinalities = new Array[Int](varSet2.vars.length)
    for (i <- 0 until varMapping.length) {
      varMapping(i) = find(varSet2.vars(i))
      cardinalities(i) = varSet2.vars(i).cardinality
    }
    scan(new Scanner() {

      override def scan(index: Int, varAssignment: VarAssignment) {
        var index2 = 0
        for (i <- 0 until varMapping.length) {
          val cardinality = cardinalities(i)
          val `val` = varAssignment.values(varMapping(i))
          index2 *= cardinality
          index2 += `val`
        }
        scanner.scan(index, index2)
      }
    })
  }

  def getAssignment(idxParam: Int): VarAssignment = {
    assert(idxParam < cardinality)
    val values: Array[Int] = Array.fill(vars.length)(-1)
    var i = vars.length - 1
    var idx = idxParam
    while (i >= 0) {
      val `var` = vars(i)
      val cardinality = `var`.cardinality
      val varValue = idx % cardinality
      values(i) = varValue
      idx = idx / cardinality
      i -= 1
    }
    new VarAssignment(vars, values)
  }

  def removeVars(otherVars: VarSet): VarSet = {
    new VarSet(set.diff(otherVars.set).toList)
  }

  def removeVars(vars: Iterable[Var]): VarSet = {
    new VarSet(set.diff(vars.toSet).toList)
  }

  override def iterator: Iterator[Var] = vars.iterator

  override def equals(obj: Any): Boolean = {
    if (obj == null) return false
    if (obj.isInstanceOf[VarSet]) {
      val other = obj.asInstanceOf[VarSet]
      return set == other.set
    }
    if (obj.isInstanceOf[Set[_]]) {
      return set == obj
    }
    throw new IllegalArgumentException(obj.getClass.getName)
  }

  override def hashCode(): Int = Objects hashCode set

  def assignments(): Iterable[VarAssignment] = (0 until cardinality).map(i => getAssignment(i))

  override def size: Int = vars.length

  def varSet: Set[Var] = set

  def intersect(varSet: VarSet): VarSet = VarSet.intersect(List(this, varSet))

  def containsAll(other: VarSet): Boolean = other.set.subsetOf(set)
}
