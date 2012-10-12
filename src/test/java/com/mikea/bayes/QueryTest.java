package com.mikea.bayes;

import com.mikea.bayes.io.HuginNetFile;
import com.mikea.bayes.query.QueryAlgorithm;
import com.mikea.bayes.query.VarElimination;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collection;

import static com.mikea.bayes.Utils.strings;
import static com.mikea.bayes.Var.vars;
import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
@RunWith(Parameterized.class)
public class QueryTest {
    private final QueryAlgorithm algorithm;

    public QueryTest(QueryAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { new VarElimination(new SumProduct.MinNeighborsStrategy()) },
                { new VarElimination(new SumProduct.MinWeightStrategy()) },
        });
    }

    @Test
    public void testQueryLinear() throws Exception {
        int size = 10;
        Var[] vars = new Var[size];
        for (int i = 0; i < size; ++i) {
            vars[i] = new Var(String.valueOf(i), 2);
        }

        BayesianNetwork.Builder networkBuilder = BayesianNetwork.withVariables(vars);

        // 0 <- 1 <- 2 <- 3 <- ... <- n
        for (int i = size - 1; i > 0; --i) {
            networkBuilder = networkBuilder.edge(vars[i], vars[i-1]);

            Factor factor = Factor
                    .withVariables(vars[i], vars[i - 1])
                    .uniform(0.25)
                    .build();
            networkBuilder = networkBuilder.factor(vars[i - 1], factor);
        }

        Var lastVar = vars[vars.length - 1];
        networkBuilder.factor(lastVar, Factor.withVariables(lastVar).uniform(0.5).build());

        BayesianNetwork network = networkBuilder.build();

        assertEquals("Factor({0(2)}, [0.5, 0.5])", network.query(algorithm, vars[0]).toString());
        assertEquals("Factor({0(2), 1(2)}, [0.25, 0.25, 0.25, 0.25])", network.query(algorithm, vars[0], vars[1]).toString());
        assertEquals("Factor({0(2)}, [0.5, 0.5])", network.query(algorithm, newVarSet(vars[0]), new Var[] {vars[1]}, new int[] {0}).toString());
    }

    @Test
    public void testCysticFibrosisBayesNet() throws Exception {
        BayesianNetwork network = HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNet.net"));

        Var jasonGenotype = network.getVarByName("JasonGenotype");
        Var jasonPhenotype = network.getVarByName("JasonPhenotype");
        Var evaPhenotype = network.getVarByName("EvaPhenotype");
        Var reneGenotype = network.getVarByName("ReneGenotype");


        // Cross checked with SamIam hugin algo.
        assertEquals("Factor({JasonGenotype(3, [FF, Ff, ff])}, [0.0100, 0.1800, 0.8100])",
                network.query(algorithm, jasonGenotype).toString("%.4f"));
        assertEquals("Factor({JasonPhenotype(2, [CysticFibrosis, NoCysticFibrosis])}, [0.1970, 0.8030])",
                network.query(algorithm, jasonPhenotype).toString("%.4f"));
        assertEquals("Factor({JasonPhenotype(2, [CysticFibrosis, NoCysticFibrosis])}, [0.2475, 0.7525])",
                network.query(algorithm, vars(jasonPhenotype), vars(evaPhenotype), strings("CysticFibrosis")).toString("%.4f"));
        assertEquals("Factor({JasonPhenotype(2, [CysticFibrosis, NoCysticFibrosis])}, [0.6415, 0.3585])",
                network.query(algorithm, vars(jasonPhenotype), vars(evaPhenotype, reneGenotype), strings("CysticFibrosis", "FF")).toString("%.4f"));
    }
}
