package com.mikea.bayes.eval.uai06;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Factor;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.SumProduct;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarAssignment;
import com.mikea.bayes.belief.CliqueTree;
import com.mikea.bayes.belief.ClusterGraph;
import com.mikea.bayes.belief.ClusterGraphs;
import com.mikea.bayes.query.VarElimination;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.StrictMath.log;

/**
 * http://melodi.ee.washington.edu/~bilmes/uai06InferenceEvaluation/
 *
 * @author mike.aizatsky@gmail.com
 */
public final class UAI06Test {
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
//        BayesianNetwork network = XBIFReader.read(new FileInputStream("./testData/uai06/test.xbif"), new VarAssignment.Builder());
        VarAssignment.Builder evidenceBuilder = new VarAssignment.Builder();
        BayesianNetwork network = XBIFReader.read(new FileInputStream("./testData/uai06/BN_0.xbif"), evidenceBuilder);
        VarAssignment evidence = evidenceBuilder.build();

        ClusterGraph clusterGraph = CliqueTree.buildCliqueTree(network, new SumProduct.MinFillStrategy());
        long maxCardinality = ClusterGraphs.maxCardinality(clusterGraph);
        System.out.println("maxCardinality = " + maxCardinality);

        double probability = new VarElimination(new SumProduct.MinFillStrategy()).prepare(network).getProbability(evidence);
        double score = log(probability);
        System.out.println("score = " + score);
    }

    private static final class XBIFReader {
        public static BayesianNetwork read(FileInputStream inputStream, VarAssignment.Builder evidence) throws XMLStreamException {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(inputStream);

            ProbabilitySpace space = new ProbabilitySpace();
            BayesianNetwork.Builder builder = new BayesianNetwork.Builder();

            String name = null;
            int values = -1;
            int observed = -1;
            String forText = null;
            List<String> given = newArrayList();
            String table = null;

            Map<String, Var> vars = newHashMap();

            while (reader.hasNext()) {
                XMLEvent e = reader.nextEvent();

                switch (e.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT: {
                        StartElement startElement = e.asStartElement();
                        String elementName = startElement.getName().toString().toLowerCase();

                        if (elementName.equals("name")) {
                            name = reader.getElementText();
                        }
                        if (elementName.equals("values")) {
                            values = Integer.parseInt(reader.getElementText());
                        }
                        if (elementName.equals("observed")) {
                            observed = Integer.parseInt(reader.getElementText());
                        }
                        if (elementName.equals("for")) {
                            forText = reader.getElementText();
                        }
                        if (elementName.equals("given")) {
                            given.add(reader.getElementText());
                        }
                        if (elementName.equals("table")) {
                            table = reader.getElementText();
                        }
                        break;
                    }
                    case XMLStreamConstants.END_ELEMENT:
                        EndElement endElement = e.asEndElement();
                        String elementName = endElement.getName().toString().toLowerCase();

                        if (elementName.equals("variable")) {
                            checkNotNull(name);
                            checkState(values > 0);

                            Var var = space.newVar(name, values);
                            vars.put(name, var);
                            builder.addVariable(var);

                            name = null;
                            values = -1;
                            if (observed >= 0) {
                                evidence = evidence.at(var, observed);
                            }
                            observed = -1;
                        }
                        if (elementName.equals("probability")) {
                            checkNotNull(forText);
                            checkNotNull(table);
                            String[] probString = table.trim().split("[ \n\t]+");
                            double[] probs = new double[probString.length];
                            for (int i = 0; i < probs.length; i++) {
                                probs[i] = Double.parseDouble(probString[i]);
                            }

                            List<Var> factorVars = newArrayList();
                            Var forVar = vars.get(forText);
                            for (String s : given) {
                                builder.edge(forVar, vars.get(s));
                                factorVars.add(vars.get(s));
                            }
                            factorVars.add(forVar);
                            Factor factor = Factor.newFactor(factorVars, probs);
                            builder.factor(forVar, factor);

                            forText = null;
                            table = null;
                            given.clear();
                        }
                        break;
                    default:
                        break;
                }
            }

            return builder.build();
        }
    }
}
