package com.mikea.bayes


object VarAssignment {
  def at(`var`: Var, value: Int): Builder = new Builder().at(`var`, value)

  def at(`var`: Var, value: String): Builder = new Builder().at(`var`, value)

  class Builder {
    private var vars = List.empty[Var]
    private var values = List.empty[Int]

    def at(`var`: Var, value: Int): Builder = {
      val i = vars.indexOf(`var`)
      if (i < 0) {
        vars = vars :+ `var`
        values = values :+ value
      } else {
        values = values.updated(i, value)
      }

      this
    }

    def at(`var`: Var, value: String): Builder = at(`var`, `var`.getValueIndex(value))

    def build(): VarAssignment = {
      new VarAssignment(vars, values)
    }
  }
}

/**
 * @author mike.aizatsky@gmail.com
 */
class VarAssignment(val vars: Seq[Var], val values: Seq[Int])
  extends Iterable[Var] {

  def get(`var`: Var): Int = {
    for (i <- 0 until vars.length if vars(i) == `var`) {
      return values(i)
    }
    throw new NoSuchElementException("Var " + `var` + " not found in " + this)
  }

  override def iterator: Iterator[Var] = vars.iterator

  override def toString(): String = {
    val result = new StringBuilder("{")
    for (i <- 0 until vars.length) {
      if (i > 0) result.append(", ")
      val `var` = vars(i)
      result.append(`var`.name)
      result.append("=")
      result.append(`var`.getValue(values(i)))
    }
    result.append("}")
    result.toString()
  }

  def containsAll(otherVars: Iterable[Var]): Boolean = {
    for (otherVar <- otherVars) {
      if (!vars.contains(otherVar)) return false
    }
    true
  }

  def contains(`var`: Var): Boolean = {
    vars.contains(`var`)
  }

  def set(`var`: Var, value: Int): VarAssignment = {
    val i = vars.indexOf(`var`)
    if (i < 0) {
      new VarAssignment(vars :+ `var`, values :+ value)
    } else {
      new VarAssignment(vars , values.updated(i, value))
    }
  }

  def set(`var`: Var, value: String): VarAssignment = set(`var`, `var`.getValueIndex(value))
}
