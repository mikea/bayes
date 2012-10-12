package com.mikea.bayes.benchmarks.query;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.SumProduct;
import com.mikea.bayes.Var;
import com.mikea.bayes.io.HuginNetFile;
import com.mikea.bayes.query.QueryAlgorithm;
import com.mikea.bayes.query.VarElimination;

import java.io.FileInputStream;

import static com.mikea.bayes.Utils.strings;
import static com.mikea.bayes.Var.vars;

/**
 * @author mike.aizatsky@gmail.com
 */
public class QueryBenchmark extends SimpleBenchmark {
    @Param({"cysticFibrosisBayesNet", "cysticFibrosisBayesNetGeneCopy"})
    private String network;

    @Param({"VarElimination.MinNeighbors", "VarElimination.MinWeight"})
    private String algorithm;

    private Test test;

    @Override
    protected void setUp() throws Exception {
        QueryAlgorithm queryAlgorithm;

        if (algorithm.equals("VarElimination.MinNeighbors")) {
            queryAlgorithm = new VarElimination(new SumProduct.MinNeighborsStrategy());
        } else if (algorithm.equals("VarElimination.MinWeight")) {
            queryAlgorithm = new VarElimination(new SumProduct.MinWeightStrategy());
        } else {
            throw new IllegalArgumentException(algorithm);
        }

        if (network.equals("cysticFibrosisBayesNet")) {
            test = new CysticFibrosisBayesNet(queryAlgorithm);
        } else if (network.equals("cysticFibrosisBayesNetGeneCopy")) {
            test = new CysticFibrosisBayesNetGeneCopy(queryAlgorithm);
        } else {
            throw new IllegalArgumentException(network);
        }

        test.setUp();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void time1(int reps) {
        for (int i = 0; i < reps; ++i) {
            test.time1();
        }
    }


    @SuppressWarnings("UnusedDeclaration")
    public void time2(int reps) {
        for (int i = 0; i < reps; ++i) {
            test.time2();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void time3(int reps) {
        for (int i = 0; i < reps; ++i) {
            test.time3();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void time4(int reps) {
        for (int i = 0; i < reps; ++i) {
            test.time4();
        }
    }


    private interface Test {
        void setUp() throws Exception;

        void time1();

        void time2();

        void time3();

        void time4();
    }

    private static class CysticFibrosisBayesNet implements Test {
        private final QueryAlgorithm queryAlgorithm;
        private BayesianNetwork network;
        private Var jasonGenotype;
        private Var jasonPhenotype;
        private Var reneGenotype;
        private Var evaPhenotype;

        public CysticFibrosisBayesNet(QueryAlgorithm queryAlgorithm) {
            this.queryAlgorithm = queryAlgorithm;
        }

        @Override
        public void setUp() throws Exception {
            network = HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNet.net"));

            jasonGenotype = network.getVarByName("JasonGenotype");
            jasonPhenotype = network.getVarByName("JasonPhenotype");
            evaPhenotype = network.getVarByName("EvaPhenotype");
            reneGenotype = network.getVarByName("ReneGenotype");
        }

        @Override
        public void time1() {
            network.query(queryAlgorithm, jasonGenotype);
        }

        @Override
        public void time2() {
            network.query(queryAlgorithm, jasonPhenotype);
        }

        @Override
        public void time3() {
            network.query(queryAlgorithm, vars(jasonPhenotype), vars(evaPhenotype), strings("CysticFibrosis"));
        }

        @Override
        public void time4() {
            network.query(queryAlgorithm, vars(jasonPhenotype), vars(evaPhenotype, reneGenotype), strings("CysticFibrosis", "FF"));
        }
    }

    private static class CysticFibrosisBayesNetGeneCopy implements Test {
        private final QueryAlgorithm queryAlgorithm;
        private BayesianNetwork network;
        private Var jasonPhenotype;
        private Var robinPhenotype;
        private Var renePhenotype;
        private Var evaPhenotype;

        public CysticFibrosisBayesNetGeneCopy(QueryAlgorithm queryAlgorithm) {
            this.queryAlgorithm = queryAlgorithm;
        }

        @Override
        public void setUp() throws Exception {
            network = HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNetGeneCopy.net"));

            jasonPhenotype = network.getVarByName("JasonPhenotype");
            evaPhenotype = network.getVarByName("EvaPhenotype");
            renePhenotype = network.getVarByName("RenePhenotype");
            robinPhenotype = network.getVarByName("RobinPhenotype");
        }

        @Override
        public void time1() {
            network.query(queryAlgorithm, robinPhenotype);
        }

        @Override
        public void time2() {
            network.query(queryAlgorithm, jasonPhenotype);
        }

        @Override
        public void time3() {
            network.query(queryAlgorithm, vars(jasonPhenotype), vars(evaPhenotype), strings("CysticFibrosis"));
        }

        @Override
        public void time4() {
            network.query(queryAlgorithm, vars(jasonPhenotype), vars(evaPhenotype, renePhenotype), strings("CysticFibrosis", "NoCysticFibrosis"));
        }
    }

    public static void main(String[] args) {
        Runner.main(QueryBenchmark.class, args);
    }

}
