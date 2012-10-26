package com.mikea.bayes;

import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static com.mikea.bayes.Factor.newFactor;
import static com.mikea.bayes.SumProduct.sumProductVariableElimination;
import static com.mikea.bayes.Var.vars;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class SumProductTest {
    private static final ProbabilitySpace space = new ProbabilitySpace("SumProductTest");
    private static final Var var1 = space.newVar("1", 2);
    private static final Var var2 = space.newVar("2", 2);
    private static final Var var3 = space.newVar("3", 2);

    @Test
    public void testSumProduct() throws Exception {
        Factor f1 = newFactor(vars(var1), new double[]{0.11, 0.89});
        Factor f2 = newFactor(vars(var1, var2), new double[]{0.59, 0.41, 0.22, 0.78});
        Factor f3 = newFactor(vars(var2, var3), new double[]{0.39, 0.61, 0.06, 0.94});

        assertEquals("Factor({}, [1.0])",
                sumProductVariableElimination(newArrayList(var1, var2, var3), newArrayList(f1, f2, f3)).toString());
    }

    @Test
    public void testMinNeighbors() throws Exception {
        Factor f1 = newFactor(vars(var1), new double[]{0.11, 0.89});
        Factor f2 = newFactor(vars(var1, var2), new double[]{0.59, 0.41, 0.22, 0.78});
        Factor f3 = newFactor(vars(var2, var3), new double[]{0.39, 0.61, 0.06, 0.94});

        assertEquals("Factor({}, [1.0])",
                sumProductVariableElimination(
                        newArrayList(var1, var2, var3),
                        newArrayList(f1, f2, f3),
                        new SumProduct.MinNeighborsStrategy()).toString());
    }
}
