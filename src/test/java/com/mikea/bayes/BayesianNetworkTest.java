package com.mikea.bayes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class BayesianNetworkTest {
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
                    .uniform(0.5)
                    .build();
            networkBuilder = networkBuilder.factor(vars[i - 1], factor);
        }

        Var lastVar = vars[vars.length - 1];
        networkBuilder.factor(lastVar, Factor.withVariables(lastVar).uniform(0.5).build());

        BayesianNetwork network = networkBuilder.build();
        Var queryVar = vars[0];

        Factor f = network.query(queryVar);
        assertEquals("Factor({0(2)}, [0.5, 0.5])", f.toString());
    }
}
