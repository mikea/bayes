package com.mikea.bayes.belief;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.StudentsNetwork;
import com.mikea.bayes.io.HuginNetFile;
import org.junit.Test;

import java.io.FileInputStream;

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
                "DataGraphImpl{isDirected=false, [{D, G, I, L}, {G}, {I, S}], [\n" +
                        "    {D, G, I, L}<->{G}:{G}\n" +
                        "    {I, S}<->{D, G, I, L}:{I}\n" +
                        "]}",
                graph.toString()
        );
    }

    @Test
    public void testFibrosis() throws Exception {
        BayesianNetwork network = HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNet.net"));

        ClusterGraph graph = CliqueTree.buildCliqueTree(network);
        assertEquals(
                "DataGraphImpl{isDirected=false, [" +
                        "{AaronGenotype, AaronPhenotype, EvaGenotype, SandraGenotype}, " +
                        "{AaronGenotype}, {BenjaminGenotype, BenjaminPhenotype}, " +
                        "{BenjaminGenotype, JamesGenotype, ReneGenotype}, " +
                        "{EvaGenotype, EvaPhenotype}, " +
                        "{EvaGenotype, IraGenotype, RobinGenotype}, " +
                        "{IraGenotype, IraPhenotype}, " +
                        "{IraGenotype, JamesGenotype, RobinGenotype}, " +
                        "{JamesGenotype, JamesPhenotype}, " +
                        "{JamesGenotype, JasonGenotype, ReneGenotype}, " +
                        "{JasonGenotype, JasonPhenotype}, " +
                        "{ReneGenotype, RenePhenotype}, " +
                        "{RobinGenotype, RobinPhenotype}, " +
                        "{SandraGenotype, SandraPhenotype}], [\n" +
                        "    {AaronGenotype, AaronPhenotype, EvaGenotype, SandraGenotype}<->{AaronGenotype}:{AaronGenotype}\n" +
                        "    {AaronGenotype, AaronPhenotype, EvaGenotype, SandraGenotype}<->{EvaGenotype, IraGenotype, RobinGenotype}:{EvaGenotype}\n" +
                        "    {BenjaminGenotype, BenjaminPhenotype}<->{BenjaminGenotype, JamesGenotype, ReneGenotype}:{BenjaminGenotype}\n" +
                        "    {BenjaminGenotype, JamesGenotype, ReneGenotype}<->{IraGenotype, JamesGenotype, RobinGenotype}:{JamesGenotype}\n" +
                        "    {EvaGenotype, EvaPhenotype}<->{AaronGenotype, AaronPhenotype, EvaGenotype, SandraGenotype}:{EvaGenotype}\n" +
                        "    {IraGenotype, IraPhenotype}<->{EvaGenotype, IraGenotype, RobinGenotype}:{IraGenotype}\n" +
                        "    {IraGenotype, JamesGenotype, RobinGenotype}<->{EvaGenotype, IraGenotype, RobinGenotype}:{IraGenotype, RobinGenotype}\n" +
                        "    {JamesGenotype, JamesPhenotype}<->{IraGenotype, JamesGenotype, RobinGenotype}:{JamesGenotype}\n" +
                        "    {JamesGenotype, JasonGenotype, ReneGenotype}<->{BenjaminGenotype, JamesGenotype, ReneGenotype}:{JamesGenotype, ReneGenotype}\n" +
                        "    {JasonGenotype, JasonPhenotype}<->{JamesGenotype, JasonGenotype, ReneGenotype}:{JasonGenotype}\n" +
                        "    {ReneGenotype, RenePhenotype}<->{BenjaminGenotype, JamesGenotype, ReneGenotype}:{ReneGenotype}\n" +
                        "    {RobinGenotype, RobinPhenotype}<->{EvaGenotype, IraGenotype, RobinGenotype}:{RobinGenotype}\n" +
                        "    {SandraGenotype, SandraPhenotype}<->{AaronGenotype, AaronPhenotype, EvaGenotype, SandraGenotype}:{SandraGenotype}\n" +
                        "]}",
                graph.toString()
        );
    }
}
