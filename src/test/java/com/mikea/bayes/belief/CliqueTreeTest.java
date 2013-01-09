package com.mikea.bayes.belief;

import com.google.common.collect.Iterables;
import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.SumProduct;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarSet;
import com.mikea.bayes.data.StudentsNetwork;
import com.mikea.bayes.io.HuginNetFile;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class CliqueTreeTest {
    @Test
    public void testStudentsNetwork() throws Exception {
        BayesianNetwork network = StudentsNetwork.buildStudentsNetwork();

        ClusterGraph graph = CliqueTree.buildCliqueTree(network);

        assertEquals(
                "DataGraphImpl{isDirected=false, [{D, G, I}, {G, L}, {I, S}], [\n" +
                        "    {G, L}<->{D, G, I}:{G}\n" +
                        "    {I, S}<->{D, G, I}:{I}\n" +
                        "]}",
                graph.toString()
        );
    }

    @Test
    public void testFibrosis() throws Exception {
        BayesianNetwork network = HuginNetFile.loadNetFile("./testData/cysticFibrosisBayesNet.net");

        ClusterGraph graph = CliqueTree.buildCliqueTree(network);
        assertEquals(
                "DataGraphImpl{isDirected=false, [{AaronGenotype, AaronPhenotype}, {AaronGenotype, EvaGenotype, SandraGenotype}, {BenjaminGenotype, BenjaminPhenotype}, {BenjaminGenotype, JamesGenotype, ReneGenotype}, {EvaGenotype, EvaPhenotype}, {EvaGenotype, IraGenotype, RobinGenotype}, {IraGenotype, IraPhenotype}, {IraGenotype, JamesGenotype, RobinGenotype}, {JamesGenotype, JamesPhenotype}, {JamesGenotype, JasonGenotype, ReneGenotype}, {JasonGenotype, JasonPhenotype}, {ReneGenotype, RenePhenotype}, {RobinGenotype, RobinPhenotype}, {SandraGenotype, SandraPhenotype}], [\n" +
                        "    {AaronGenotype, AaronPhenotype}<->{AaronGenotype, EvaGenotype, SandraGenotype}:{AaronGenotype}\n" +
                        "    {BenjaminGenotype, BenjaminPhenotype}<->{BenjaminGenotype, JamesGenotype, ReneGenotype}:{BenjaminGenotype}\n" +
                        "    {BenjaminGenotype, JamesGenotype, ReneGenotype}<->{IraGenotype, JamesGenotype, RobinGenotype}:{JamesGenotype}\n" +
                        "    {EvaGenotype, EvaPhenotype}<->{AaronGenotype, EvaGenotype, SandraGenotype}:{EvaGenotype}\n" +
                        "    {EvaGenotype, IraGenotype, RobinGenotype}<->{AaronGenotype, EvaGenotype, SandraGenotype}:{EvaGenotype}\n" +
                        "    {IraGenotype, IraPhenotype}<->{EvaGenotype, IraGenotype, RobinGenotype}:{IraGenotype}\n" +
                        "    {IraGenotype, JamesGenotype, RobinGenotype}<->{EvaGenotype, IraGenotype, RobinGenotype}:{IraGenotype, RobinGenotype}\n" +
                        "    {JamesGenotype, JamesPhenotype}<->{IraGenotype, JamesGenotype, RobinGenotype}:{JamesGenotype}\n" +
                        "    {JamesGenotype, JasonGenotype, ReneGenotype}<->{BenjaminGenotype, JamesGenotype, ReneGenotype}:{JamesGenotype, ReneGenotype}\n" +
                        "    {JasonGenotype, JasonPhenotype}<->{JamesGenotype, JasonGenotype, ReneGenotype}:{JasonGenotype}\n" +
                        "    {ReneGenotype, RenePhenotype}<->{BenjaminGenotype, JamesGenotype, ReneGenotype}:{ReneGenotype}\n" +
                        "    {RobinGenotype, RobinPhenotype}<->{EvaGenotype, IraGenotype, RobinGenotype}:{RobinGenotype}\n" +
                        "    {SandraGenotype, SandraPhenotype}<->{AaronGenotype, EvaGenotype, SandraGenotype}:{SandraGenotype}\n" +
                        "]}",
                graph.toString()
        );
    }

    @Test
    public void testBipartiteGraph10() throws Exception {
        ClusterGraph cliqueTree = buildCliqueTreeForBipartiteGraph(10);

        assertEquals(
                "DataGraphImpl{isDirected=false, [{x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x1, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x2, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x3, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x4, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x5, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x6, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x7, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x8, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}, {x9, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}], [\n" +
                        "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x1, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" +
                        "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x2, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" +
                        "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x3, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" +
                        "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x4, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" +
                        "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x5, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" +
                        "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x6, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" +
                        "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x7, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" +
                        "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x8, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" +
                        "    {x0, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}<->{x9, y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}:{y0, y1, y2, y3, y4, y5, y6, y7, y8, y9}\n" +
                        "]}",
                cliqueTree.toString()
        );
    }

    @Test
    public void testBipartiteGraph2() throws Exception {
        ClusterGraph cliqueTree = buildCliqueTreeForBipartiteGraph(2);

        assertEquals(
                "DataGraphImpl{isDirected=false, [{x0, y0, y1}, {x1, y0, y1}], [\n" +
                        "    {x0, y0, y1}<->{x1, y0, y1}:{y0, y1}\n" +
                        "]}",
                cliqueTree.toString()
        );
    }

    @Test
    public void testBipartiteGraph3() throws Exception {
        ClusterGraph cliqueTree = buildCliqueTreeForBipartiteGraph(3);

        assertEquals(
                "DataGraphImpl{isDirected=false, [{x0, y0, y1, y2}, {x1, y0, y1, y2}, {x2, y0, y1, y2}], [\n" +
                        "    {x0, y0, y1, y2}<->{x1, y0, y1, y2}:{y0, y1, y2}\n" +
                        "    {x0, y0, y1, y2}<->{x2, y0, y1, y2}:{y0, y1, y2}\n" +
                        "]}",
                cliqueTree.toString()
        );
    }

    private static ClusterGraph buildCliqueTreeForBipartiteGraph(int nVars) {
        ProbabilitySpace space = new ProbabilitySpace();

        List<Var> xVars = newArrayList();
        List<Var> yVars = newArrayList();

        for (int i = 0; i < nVars; ++i) {
            xVars.add(space.newVar("x" + i, 2));
            yVars.add(space.newVar("y" + i, 2));
        }

        List<VarSet> scopes = newArrayList();
        for (int i = 0; i < nVars; ++i) {
            for (int j = 0; j < nVars; ++j) {
                scopes.add(VarSet.newVarSet(xVars.get(i), yVars.get(j)));
            }
        }

        return CliqueTree.buildCliqueTree(Iterables.concat(xVars, yVars), scopes, SumProduct.DEFAULT_STRATEGY);
    }

}
