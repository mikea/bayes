package com.mikea.bayes.benchmarks;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.mikea.bayes.Factor;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarSet;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author mike.aizatsky@gmail.com
 */
public class FactorProductBenchmark extends SimpleBenchmark {
    private Factor factor1;
    private Factor factor2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.

        ProbabilitySpace space = new ProbabilitySpace();
        Collection<Var> vars1 = newArrayList();
        Collection<Var> vars2 = newArrayList();

        for (int i = 0; i < 10; ++i) {
            vars1.add(space.newVar("a" + i, 2));
            vars2.add(space.newVar("b" + i, 2));
        }

        factor1 = Factor.constant(VarSet.newVarSet(vars1), 1);
        factor2 = Factor.constant(VarSet.newVarSet(vars2), 1);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void time1(int reps) {
        for (int i = 0; i < reps; ++i) {
            Factor f = factor1.product(factor2);
        }
    }

    public static void main(String[] args) {
        Runner.main(FactorProductBenchmark.class, args);
    }
}
