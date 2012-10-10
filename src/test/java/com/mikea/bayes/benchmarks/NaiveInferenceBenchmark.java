package com.mikea.bayes.benchmarks;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Factor;
import com.mikea.bayes.Var;

/**
 * @author mike.aizatsky@gmail.com
 */
public class NaiveInferenceBenchmark extends SimpleBenchmark {
    @Param({"1", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"})
    int size;

    private BayesianNetwork network;
    private Var queryVar;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

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

        network = networkBuilder.build();
        queryVar = vars[0];
    }

    @SuppressWarnings("UnusedDeclaration")
    public void timeQuery(int reps) {
        for (int i = 0; i < reps; ++i) {
            network.query(queryVar);
        }
    }

    public static void main(String[] args) {
        Runner.main(NaiveInferenceBenchmark.class, args);
    }
}
