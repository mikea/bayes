
package com.mikea.bayes.belief

import org.junit.Test

/**
 * @author mike.aizatsky@gmail.com
 */
object CliqueTreeTest {
  private def buildCliqueTreeForBipartiteGraph(nVars: Int): ClusterGraph = ??? /*{
    val space: ProbabilitySpace = new ProbabilitySpace
    val xVars: List[Var] = newArrayList
    val yVars: List[Var] = newArrayList
    {
      var i: Int = 0
      while (i < nVars) {
        {
          xVars.add(space.newVar("x" + i, 2))
          yVars.add(space.newVar("y" + i, 2))
        }
        ({
          i += 1; i
        })
      }
    }
    val scopes: List[VarSet] = newArrayList
    {
      var i: Int = 0
      while (i < nVars) {
        {
          {
            var j: Int = 0
            while (j < nVars) {
              {
                scopes.add(VarSet.newVarSet(xVars.get(i), yVars.get(j)))
              }
              ({
                j += 1; j
              })
            }
          }
        }
        ({
          i += 1; i
        })
      }
    }
    return CliqueTree.buildCliqueTree(Iterables.concat(xVars, yVars), scopes, defaultOptions)
  }
*/}

class CliqueTreeTest {
  @Test def testStudentsNetwork {
    ???
/*
    val network: BayesianNetwork = StudentsNetwork.buildStudentsNetwork
    val graph: ClusterGraph = CliqueTree.buildCliqueTree(network)
    assertEquals("DataGraphImpl{isDirected=false, [{D, G, I}, {G, L}, {I, S}], [\n" + "    {G, L}<->{D, G, I}:{G}\n" + "    {I, S}<->{D, G, I}:{I}\n" + "]}", graph.toString)
*/
  }

  @Test def testFibrosis {
    ???
/*
    val network: BayesianNetwork = HuginNetFile.loadNetFile("./testData/cysticFibrosisBayesNet.net")
    val graph: ClusterGraph = CliqueTree.buildCliqueTree(network)
    assertEquals("DataGraphImpl{isDirected=false, [{AaronGenotype, AaronPhenotype}, {AaronGenotype, EvaGenotype, SandraGenotype}, {BenjaminGenotype, BenjaminPhenotype}, {BenjaminGenotype, JamesGenotype, ReneGenotype}, {EvaGenotype, EvaPhenotype}, {EvaGenotype, IraGenotype, RobinGenotype}, {IraGenotype, IraPhenotype}, {IraGenotype, JamesGenotype, RobinGenotype}, {JamesGenotype, JamesPhenotype}, {JamesGenotype, JasonGenotype, ReneGenotype}, {JasonGenotype, JasonPhenotype}, {ReneGenotype, RenePhenotype}, {RobinGenotype, RobinPhenotype}, {SandraGenotype, SandraPhenotype}], [\n" + "    {AaronGenotype, AaronPhenotype}<->{AaronGenotype, EvaGenotype, SandraGenotype}:{AaronGenotype}\n" + "    {BenjaminGenotype, BenjaminPhenotype}<->{BenjaminGenotype, JamesGenotype, ReneGenotype}:{BenjaminGenotype}\n" + "    {BenjaminGenotype, JamesGenotype, ReneGenotype}<->{IraGenotype, JamesGenotype, RobinGenotype}:{JamesGenotype}\n" + "    {EvaGenotype, EvaPhenotype}<->{AaronGenotype, EvaGenotype, SandraGenotype}:{EvaGenotype}\n" + "    {EvaGenotype, IraGenotype, RobinGenotype}<->{AaronGenotype, EvaGenotype, SandraGenotype}:{EvaGenotype}\n" + "    {IraGenotype, IraPhenotype}<->{EvaGenotype, IraGenotype, RobinGenotype}:{IraGenotype}\n" + "    {IraGenotype, JamesGenotype, RobinGenotype}<->{EvaGenotype, IraGenotype, RobinGenotype}:{IraGenotype, RobinGenotype}\n" + "    {JamesGenotype, JamesPhenotype}<->{IraGenotype, JamesGenotype, RobinGenotype}:{JamesGenotype}\n" + "    {JamesGenotype, JasonGenotype, ReneGenotype}<->{BenjaminGenotype, JamesGenotype, ReneGenotype}:{JamesGenotype, ReneGenotype}\n" + "    {JasonGenotype, JasonPhenotype}<->{JamesGenotype, JasonGenotype, ReneGenotype}:{JasonGenotype}\n" + "    {ReneGenotype, RenePhenotype}<->{BenjaminGenotype, JamesGenotype, ReneGenotype}:{ReneGenotype}\n" + "    {RobinGenotype, RobinPhenotype}<->{EvaGenotype, IraGenotype, RobinGenotype}:{RobinGenotype}\n" + "    {SandraGenotype, SandraPhenotype}<->{AaronGenotype, EvaGenotype, SandraGenotype}:{SandraGenotype}\n" + "]}", graph.toString)
*/
  }

  @Test def testBipartiteGraph10 {
    ???
/*
    val cliqueTree: ClusterGraph = buildCliqueTreeForBipartiteGraph(10)
    assertEquals("DataGraphImpl{isDirected=false, [{x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x1, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x2, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x3, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x4, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x5, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x6, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x7, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x8, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x9, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}], [\n" + "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x1, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" + "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x2, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" + "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x3, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" + "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x4, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" + "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x5, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" + "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x6, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" + "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x7, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" + "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x8, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" + "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x9, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" + "]}", cliqueTree.toString)
*/
  }

  @Test def testBipartiteGraph2 {
    ???
/*
    val cliqueTree: ClusterGraph = buildCliqueTreeForBipartiteGraph(2)
    assertEquals("DataGraphImpl{isDirected=false, [{x0, y0, y1}, {x1, y0, y1}], [\n" + "    {x0, y0, y1}<->{x1, y0, y1}:{y0, y1}\n" + "]}", cliqueTree.toString)
*/
  }

  @Test def testBipartiteGraph3 {
    ???
/*
    val cliqueTree: ClusterGraph = buildCliqueTreeForBipartiteGraph(3)
    assertEquals("DataGraphImpl{isDirected=false, [{x0, y0, y1, y2}, {x1, y0, y1, y2}, {x2, y0, y1, y2}], [\n" + "    {x0, y0, y1, y2}<->{x1, y0, y1, y2}:{y0, y1, y2}\n" + "    {x0, y0, y1, y2}<->{x2, y0, y1, y2}:{y0, y1, y2}\n" + "]}", cliqueTree.toString)
*/
  }
}