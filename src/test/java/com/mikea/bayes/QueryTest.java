package com.mikea.bayes;

import com.mikea.bayes.io.HuginNetFile;
import org.junit.Test;

import java.io.FileInputStream;

import static com.mikea.bayes.Utils.strings;
import static com.mikea.bayes.Var.vars;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class QueryTest {
    @Test
    public void testCysticFibrosisBayesNet() throws Exception {
        BayesianNetwork network = HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNet.net"));

        Var jasonGenotype = network.getVarByName("JasonGenotype");
        Var jasonPhenotype = network.getVarByName("JasonPhenotype");
        Var evaPhenotype = network.getVarByName("EvaPhenotype");
        Var reneGenotype = network.getVarByName("ReneGenotype");


        // Cross checked with SamIam hugin algo.
        assertEquals("Factor({JasonGenotype(3, [FF, Ff, ff])}, [0.009999999999999998, 0.18, 0.81])",
                network.query(jasonGenotype).toString());
        assertEquals("Factor({JasonPhenotype(2, [CysticFibrosis, NoCysticFibrosis])}, [0.19699999999999998, 0.803])",
                network.query(jasonPhenotype).toString());
        assertEquals("Factor({JasonPhenotype(2, [CysticFibrosis, NoCysticFibrosis])}, [0.24745939086294413, 0.7525406091370559])",
                network.query(vars(jasonPhenotype), vars(evaPhenotype), strings("CysticFibrosis")).toString());
        assertEquals("Factor({JasonPhenotype(2, [CysticFibrosis, NoCysticFibrosis])}, [0.641472081218274, 0.3585279187817259])",
                network.query(vars(jasonPhenotype), vars(evaPhenotype, reneGenotype), strings("CysticFibrosis", "FF")).toString());
    }
}
