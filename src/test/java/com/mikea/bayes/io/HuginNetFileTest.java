package com.mikea.bayes.io;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Factor;
import com.mikea.bayes.Var;
import org.junit.Test;

import java.io.FileInputStream;

import static com.google.common.collect.Lists.transform;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class HuginNetFileTest {
    @Test
    public void testCysticFibrosisBayesNet() throws Exception {
        BayesianNetwork network = HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNet.net"));
        assertEquals("[" +
                "IraGenotype(3, [FF, Ff, ff]), " +
                "IraPhenotype(2, [CysticFibrosis, NoCysticFibrosis]), " +
                "JamesGenotype(3, [FF, Ff, ff]), " +
                "JamesPhenotype(2, [CysticFibrosis, NoCysticFibrosis]), " +
                "RobinGenotype(3, [FF, Ff, ff]), " +
                "RobinPhenotype(2, [CysticFibrosis, NoCysticFibrosis]), " +
                "EvaGenotype(3, [FF, Ff, ff]), " +
                "EvaPhenotype(2, [CysticFibrosis, NoCysticFibrosis]), " +
                "JasonGenotype(3, [FF, Ff, ff]), " +
                "JasonPhenotype(2, [CysticFibrosis, NoCysticFibrosis]), " +
                "ReneGenotype(3, [FF, Ff, ff]), " +
                "RenePhenotype(2, [CysticFibrosis, NoCysticFibrosis]), " +
                "BenjaminGenotype(3, [FF, Ff, ff]), " +
                "BenjaminPhenotype(2, [CysticFibrosis, NoCysticFibrosis]), " +
                "SandraGenotype(3, [FF, Ff, ff]), " +
                "SandraPhenotype(2, [CysticFibrosis, NoCysticFibrosis]), " +
                "AaronGenotype(3, [FF, Ff, ff]), " +
                "AaronPhenotype(2, [CysticFibrosis, NoCysticFibrosis])]",
                transform(network.getVarList(), Var.TO_STRING).toString());

        Var jasonGenotype = network.getVarByName("JasonGenotype");
        Factor jasonGenotypeFactor = network.getFactor(jasonGenotype);

        assertEquals(
                "Factor({ReneGenotype, JamesGenotype, JasonGenotype}):\n" +
                        "{ReneGenotype=FF, JamesGenotype=FF, JasonGenotype=FF}: 1.000000\n" +
                        "{ReneGenotype=FF, JamesGenotype=FF, JasonGenotype=Ff}: 0.000000\n" +
                        "{ReneGenotype=FF, JamesGenotype=FF, JasonGenotype=ff}: 0.000000\n" +
                        "{ReneGenotype=FF, JamesGenotype=Ff, JasonGenotype=FF}: 0.500000\n" +
                        "{ReneGenotype=FF, JamesGenotype=Ff, JasonGenotype=Ff}: 0.500000\n" +
                        "{ReneGenotype=FF, JamesGenotype=Ff, JasonGenotype=ff}: 0.000000\n" +
                        "{ReneGenotype=FF, JamesGenotype=ff, JasonGenotype=FF}: 0.000000\n" +
                        "{ReneGenotype=FF, JamesGenotype=ff, JasonGenotype=Ff}: 1.000000\n" +
                        "{ReneGenotype=FF, JamesGenotype=ff, JasonGenotype=ff}: 0.000000\n" +
                        "{ReneGenotype=Ff, JamesGenotype=FF, JasonGenotype=FF}: 0.500000\n" +
                        "{ReneGenotype=Ff, JamesGenotype=FF, JasonGenotype=Ff}: 0.500000\n" +
                        "{ReneGenotype=Ff, JamesGenotype=FF, JasonGenotype=ff}: 0.000000\n" +
                        "{ReneGenotype=Ff, JamesGenotype=Ff, JasonGenotype=FF}: 0.250000\n" +
                        "{ReneGenotype=Ff, JamesGenotype=Ff, JasonGenotype=Ff}: 0.500000\n" +
                        "{ReneGenotype=Ff, JamesGenotype=Ff, JasonGenotype=ff}: 0.250000\n" +
                        "{ReneGenotype=Ff, JamesGenotype=ff, JasonGenotype=FF}: 0.000000\n" +
                        "{ReneGenotype=Ff, JamesGenotype=ff, JasonGenotype=Ff}: 0.500000\n" +
                        "{ReneGenotype=Ff, JamesGenotype=ff, JasonGenotype=ff}: 0.500000\n" +
                        "{ReneGenotype=ff, JamesGenotype=FF, JasonGenotype=FF}: 0.000000\n" +
                        "{ReneGenotype=ff, JamesGenotype=FF, JasonGenotype=Ff}: 1.000000\n" +
                        "{ReneGenotype=ff, JamesGenotype=FF, JasonGenotype=ff}: 0.000000\n" +
                        "{ReneGenotype=ff, JamesGenotype=Ff, JasonGenotype=FF}: 0.000000\n" +
                        "{ReneGenotype=ff, JamesGenotype=Ff, JasonGenotype=Ff}: 0.500000\n" +
                        "{ReneGenotype=ff, JamesGenotype=Ff, JasonGenotype=ff}: 0.500000\n" +
                        "{ReneGenotype=ff, JamesGenotype=ff, JasonGenotype=FF}: 0.000000\n" +
                        "{ReneGenotype=ff, JamesGenotype=ff, JasonGenotype=Ff}: 0.000000\n" +
                        "{ReneGenotype=ff, JamesGenotype=ff, JasonGenotype=ff}: 1.000000\n",
                jasonGenotypeFactor.toStringAsTable("%f"));
    }

    @Test
    public void testCysticFibrosisBayesNetGeneCopy() throws Exception {
        HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNetGeneCopy.net"));
    }

    @Test
    public void testSpinalMuscularAtrophyBayesNet() throws Exception {
        HuginNetFile.loadNetFile(new FileInputStream("./testData/spinalMuscularAtrophyBayesNet.net"));
    }
}
