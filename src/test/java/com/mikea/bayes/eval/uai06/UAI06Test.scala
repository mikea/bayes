
package com.mikea.bayes.eval.uai06


/**
 * http://melodi.ee.washington.edu/~bilmes/uai06InferenceEvaluation/
 *
 * @author mike.aizatsky@gmail.com
 */
final object UAI06Test {
  def main(args: Array[String]) {
    ???
/*
    val evidenceBuilder: Nothing = new Nothing
    val network: BayesianNetwork = XBIFReader.read(new FileInputStream("./testData/uai06/BN_0.xbif"), evidenceBuilder)
    val evidence: VarAssignment = evidenceBuilder.build
    val clusterGraph: ClusterGraph = CliqueTree.buildCliqueTree(network, defaultOptions.withStrategy(new SumProduct.MinFillStrategy))
    DbgServer.publish(network, "uai06_graph_0")
    DbgServer.publish(CliqueTree.buildCliqueTree(network, defaultOptions.withStrategy(new SumProduct.MinNeighborsStrategy).dontPrune), "uai06_graph_0_cluster_graph_minneighbors")
    DbgServer.publish(CliqueTree.buildCliqueTree(network, defaultOptions.withStrategy(new SumProduct.MinNeighborsStrategy)), "uai06_graph_0_cluster_graph_minneighbors_pruned")
    DbgServer.publish(CliqueTree.buildCliqueTree(network, defaultOptions.withStrategy(new SumProduct.MinFillStrategy).dontPrune), "uai06_graph_0_cluster_graph_minfill")
    DbgServer.publish(CliqueTree.buildCliqueTree(network, defaultOptions.withStrategy(new SumProduct.MinFillStrategy)), "uai06_graph_0_cluster_graph_minfill_pruned")
    val maxCardinality: Long = ClusterGraphs.maxCardinality(clusterGraph)
    System.out.println("maxCardinality = " + maxCardinality)
    DbgServer.join
*/
  }

/*
  private final object XBIFReader {
    def read(inputStream: FileInputStream, evidence: Nothing): BayesianNetwork = {
      val factory: XMLInputFactory = XMLInputFactory.newInstance
      val reader: XMLEventReader = factory.createXMLEventReader(inputStream)
      val space: ProbabilitySpace = new ProbabilitySpace
      val builder: Nothing = new Nothing
      var name: String = null
      var values: Int = -1
      var observed: Int = -1
      var forText: String = null
      val given: List[String] = newArrayList
      var table: String = null
      val vars: Map[String, Var] = newHashMap
      while (reader.hasNext) {
        val e: XMLEvent = reader.nextEvent
        e.getEventType match {
          case XMLStreamConstants.START_ELEMENT => {
            val startElement: StartElement = e.asStartElement
            val elementName: String = startElement.getName.toString.toLowerCase
            if (elementName == "name") {
              name = reader.getElementText
            }
            if (elementName == "values") {
              values = Integer.parseInt(reader.getElementText)
            }
            if (elementName == "observed") {
              observed = Integer.parseInt(reader.getElementText)
            }
            if (elementName == "for") {
              forText = reader.getElementText
            }
            if (elementName == "given") {
              given.add(reader.getElementText)
            }
            if (elementName == "table") {
              table = reader.getElementText
            }
            break //todo: break is not supported
          }
          case XMLStreamConstants.END_ELEMENT =>
            val endElement: EndElement = e.asEndElement
            val elementName: String = endElement.getName.toString.toLowerCase
            if (elementName == "variable") {
              checkNotNull(name)
              checkState(values > 0)
              val `var`: Var = space.newVar(name, values)
              vars.put(name, `var`)
              builder.addVariable(`var`)
              name = null
              values = -1
              if (observed >= 0) {
                evidence = evidence.at(`var`, observed)
              }
              observed = -1
            }
            if (elementName == "probability") {
              checkNotNull(forText)
              checkNotNull(table)
              val probString: Array[String] = table.trim.split("[ \n\t]+")
              val probs: Array[Double] = new Array[Double](probString.length)
              {
                var i: Int = 0
                while (i < probs.length) {
                  {
                    probs(i) = Double.parseDouble(probString(i))
                  }
                  ({
                    i += 1; i - 1
                  })
                }
              }
              val factorVars: List[Var] = newArrayList
              val forVar: Var = vars.get(forText)
              import scala.collection.JavaConversions._
              for (s <- given) {
                builder.edge(forVar, vars.get(s))
                factorVars.add(vars.get(s))
              }
              factorVars.add(forVar)
              val factor: Factor = Factor.newFactor(factorVars, probs)
              builder.factor(forVar, factor)
              forText = null
              table = null
              given.clear
            }
            break //todo: break is not supported
          case _ =>
            break //todo: break is not supported
        }
      }
      return builder.build
    }
  }
*/

}