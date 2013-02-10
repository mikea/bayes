package com.mikea.bayes.io

import com.mikea.bayes.BayesianNetwork
import org.antlr.runtime.tree.Tree
import java.io.FileInputStream
import java.io.InputStream
import java.util.Set
//remove if not needed

object HuginNetFile {

  def loadNetFile(fileName: String): BayesianNetwork = {
    val ios = new FileInputStream(fileName)
    try {
      loadNetFile(ios)
    } finally {
      ios.close()
    }
  }

  def loadNetFile(stream: InputStream): BayesianNetwork = ???/*{
    val space = new ProbabilitySpace()
    val tree = parse(stream)
    val vars = newLinkedHashMap()
    visit(tree, new RecursiveTreeVisitor() {

      override def visitNode(node: Tree, name: String, attrList: Tree) {
        val states = getProperty(attrList, "states", classOf[Array[String]])
        vars.put(name, space.newVar(name, states.length, states))
      }
    })
    val builder = BayesianNetwork.withVariables(vars.values)
    visit(tree, new RecursiveTreeVisitor() {

      override def visitPotential(tree: Tree,
                                  to: String,
                                  from: Array[String],
                                  attrList: Tree) {
        val varTo = vars.get(to)
        val scope = newArrayList(varTo)
        for (f <- from) {
          val varFrom = vars.get(f)
          scope.add(varFrom)
          builder.edge(varFrom, varTo)
        }
        builder.factor(varTo, Factor.newFactor(Lists.reverse(scope), getProperty(attrList, "data", classOf[Array[Double]])))
      }
    })
    builder.build()
  }*/

  private def getProperty[C](attrList: Tree, propertyName: String, aClass: Class[C]): C = ???/*{
    val result = ObjectArrays.newArray(aClass, 1)
    visit(attrList, new RecursiveTreeVisitor() {

      override def visitAttr(node: Tree, name: String, value: Tree) {
        if (name == propertyName) {
          result(0) = convertPropertyValue(value, aClass)
        }
      }
    })
    result(0)
  }*/

  private def convertPropertyValue[C](value: Tree, aClass: Class[C]): C = ???/*{
    if (aClass == classOf[Array[String]]) {
      val result = newArrayList()
      for (i <- 0 until value.getChildCount) {
        result.add(value.getChild(i).toString)
      }
      aClass.cast(result.toArray(new Array[String](result.size)))
    } else if (aClass == classOf[Array[Double]]) {
      if (value.getType == HuginLexer.FLOAT_LIST) {
        aClass.cast(parseDoubles(getChildren(value)))
      } else if (value.getType == HuginLexer.FLOAT_MATRIX) {
        val children = getChildren(value)
        val grandChildren = Iterables.transform(children, new Function[Tree, java.lang.Iterable[Tree]]() {

          override def apply(input: Tree): java.lang.Iterable[Tree] = {
            assert input != null
            return getChildren(input)
          }
        })
        aClass.cast(parseDoubles(Iterables.concat(grandChildren)))
      } else {
        throw new UnsupportedOperationException(aClass + " - " + value.toStringTree())
      }
    } else {
      throw new UnsupportedOperationException(aClass + " - " + value.toStringTree())
    }
  }*/

/*
  private def parseDoubles(children: java.lang.Iterable[Tree]): Array[Double] = {
    val doubles = Iterables.toArray(Iterables.transform(children, new Function[Tree, Double]() {

      override def apply(input: Tree): java.lang.Double = {
        assert input != null
        return Double.parseDouble(input.toString)
      }
    }), classOf[Double])
    val result = new Array[Double](doubles.length)
    for (i <- 0 until result.length) {
      result(i) = doubles(i)
    }
    result
  }
*/

  private def visit(tree: Tree, treeVisitor: TreeVisitor) {
    ???
    /*
    var unknownType = false
    try tree.getType match {
      case HuginLexer.DOMAIN => treeVisitor.visitDomain(tree, getChildOfType(tree, HuginLexer.NET), getChildrenOfType(tree,
        ImmutableSet.of(HuginLexer.NODE, HuginLexer.POTENTIAL)))
      case HuginLexer.NET => treeVisitor.visitNet(tree)
      case HuginLexer.NODE => treeVisitor.visitNode(tree, getChildOfType(tree, HuginLexer.ID).toString,
        getChildOfType(tree, HuginLexer.ATTR_LIST))
      case HuginLexer.ATTR_LIST => treeVisitor.visitAttrList(tree, getChildrenOfType(tree, ImmutableSet.of(HuginLexer.ATTR)))
      case HuginLexer.ATTR => treeVisitor.visitAttr(tree, tree.getChild(0).toString, tree.getChild(1))
      case HuginLexer.POTENTIAL => treeVisitor.visitPotential(tree, getChildOfType(tree, HuginLexer.TO).getChild(0).toString,
        Iterables.toArray(Iterables.transform(getChildren(getChildOfType(tree, HuginLexer.FROM)), new Function[Tree, String]() {

          override def apply(tree: Tree): String = {
            assert tree != null
            return tree.toString
          }
        }), classOf[String]), getChildOfType(tree, HuginLexer.ATTR_LIST))
      case _ => unknownType = true
    } catch {
      case e: Exception => {
        Throwables.propagateIfInstanceOf(e, classOf[VisitException])
        throw new VisitException("Exception while visiting " + tree.toStringTree(), e)
      }
    }
    if (unknownType) {
      throw new IllegalStateException("Unsupported node type: " + tree.getType + " in " + tree.toStringTree())
    }*/
  }

  private def getChildren(tree: Tree): java.lang.Iterable[Tree] = ???/*{
    val result = new Array[Tree](tree.getChildCount)
    for (i <- 0 until result.length) {
      result(i) = tree.getChild(i)
    }
    asList(result)
  }*/

  private def getChildOfType(tree: Tree, `type`: Int): Tree = {
    for (i <- 0 until tree.getChildCount if tree.getChild(i).getType == `type`) return tree.getChild(i)
    throw new IllegalStateException()
  }

  private def getChildrenOfType(tree: Tree, types: Set[Integer]): Array[Tree] = ???/* {
    val result = newArrayList()
    for (i <- 0 until tree.getChildCount) {
      val child = tree.getChild(i)
      if (types.contains(child.getType)) result.add(child)
    }
    result.toArray(new Array[Tree](result.size))
  }*/

  private def parse(stream: InputStream): Tree = ???/* {
    val errorReporter = new ErrorReporter() {

      override def reportError(error: String) {
        throw Throwables.propagate(new IOException(error))
      }
    }
    val lexer = new HuginLexer(new ANTLRInputStream(stream))
    val parser = new HuginParser(new CommonTokenStream(lexer))
    lexer.setErrorReporter(errorReporter)
    parser.setErrorReporter(errorReporter)
    val domainDefinition_return = parser.domainDefinition()
    domainDefinition_return.getTree.asInstanceOf[Tree]
  }*/

  trait TreeVisitor {

    def visitDomain(node: Tree, header: Tree, elements: Array[Tree]): Unit

    def visitNet(node: Tree): Unit

    def visitNode(node: Tree, name: String, attrList: Tree): Unit

    def visitAttrList(node: Tree, attrs: Array[Tree]): Unit

    def visitAttr(node: Tree, name: String, value: Tree): Unit

    def visitPotential(tree: Tree,
                       to: String,
                       from: Array[String],
                       attrList: Tree): Unit
  }

  class RecursiveTreeVisitor extends TreeVisitor {

    override def visitDomain(node: Tree, header: Tree, elements: Array[Tree]) {
      visit(header, this)
      for (element <- elements) {
        visit(element, this)
      }
    }

    override def visitNet(node: Tree) {
    }

    override def visitNode(node: Tree, name: String, attrList: Tree) {
      visit(attrList, this)
    }

    override def visitAttrList(node: Tree, attrs: Array[Tree]) {
      for (attr <- attrs) {
        visit(attr, this)
      }
    }

    override def visitAttr(node: Tree, name: String, value: Tree) {
    }

    override def visitPotential(tree: Tree,
                                to: String,
                                from: Array[String],
                                attrList: Tree) {
      visit(attrList, this)
    }
  }

  @SerialVersionUID(6382554085991125362L)
  class VisitException(message: String, cause: Throwable) extends RuntimeException(message, cause)
}
