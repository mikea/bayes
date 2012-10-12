package com.mikea.bayes.benchmarks.query;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Var;
import com.mikea.bayes.io.HuginNetFile;

import java.io.FileInputStream;

import static com.mikea.bayes.Utils.strings;
import static com.mikea.bayes.Var.vars;

/**
 * @author mike.aizatsky@gmail.com
 */
public class CysticFibrosisBenchmark extends SimpleBenchmark {

    private BayesianNetwork network;
    private Var jasonGenotype;
    private Var jasonPhenotype;
    private Var reneGenotype;
    private Var evaPhenotype;

    @Override
    protected void setUp() throws Exception {
        network = HuginNetFile.loadNetFile(new FileInputStream("./testData/cysticFibrosisBayesNet.net"));

        jasonGenotype = network.getVarByName("JasonGenotype");
        jasonPhenotype = network.getVarByName("JasonPhenotype");
        evaPhenotype = network.getVarByName("EvaPhenotype");
        reneGenotype = network.getVarByName("ReneGenotype");
    }

    @SuppressWarnings("UnusedDeclaration")
    public void timeJasonGenotypeNoEvidence(int reps) {
        for (int i = 0; i < reps; ++i) {
            network.query(jasonGenotype);
        }
    }


    @SuppressWarnings("UnusedDeclaration")
    public void timeJasonPhenotypeNoEvidence(int reps) {
        for (int i = 0; i < reps; ++i) {
            network.query(jasonGenotype);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void timeJasonPhenotypeEvidence1(int reps) {
        for (int i = 0; i < reps; ++i) {
            network.query(vars(jasonPhenotype), vars(evaPhenotype), strings("CysticFibrosis"));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void timeJasonPhenotypeEvidence2(int reps) {
        for (int i = 0; i < reps; ++i) {
            network.query(vars(jasonPhenotype), vars(evaPhenotype, reneGenotype), strings("CysticFibrosis", "FF"));
        }
    }

    public static void main(String[] args) {
        Runner.main(CysticFibrosisBenchmark.class, args);
    }

}
