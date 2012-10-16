package com.mikea.bayes;

import org.gga.graph.maps.DataGraph;
import org.junit.Test;

import java.util.List;

import static com.mikea.bayes.Factor.newFactor;
import static com.mikea.bayes.Factor.product;
import static com.mikea.bayes.Var.vars;
import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class FactorTest {
    private static final ProbabilitySpace space = new ProbabilitySpace("VarSetTest");
    private static final Var var1 = space.newVar("1", 2);
    private static final Var var2 = space.newVar("2", 2);
    private static final Var var3 = space.newVar("3", 2);

    @Test
    public void testToString() throws Exception {
        Factor f1 = newFactor(vars(var1), new double[]{0.11, 0.89});
        Factor f2 = newFactor(vars(var2, var1), new double[]{0.59, 0.41, 0.22, 0.78});
        assertEquals("Factor({1}, [0.11, 0.89])", f1.toString());
        assertEquals("Factor({2, 1}, [0.59, 0.41, 0.22, 0.78])", f2.toString());
    }

    @Test
    public void testProduct() throws Exception {
        Factor f1 = newFactor(vars(var1), new double[]{0.11, 0.89});
        Factor f2 = newFactor(vars(var1, var2), new double[]{0.59, 0.41, 0.22, 0.78});
        Factor f3 = newFactor(vars(var2, var3), new double[]{0.39, 0.61, 0.06, 0.94});

        assertEquals("Factor({1, 2}, [0.0649, 0.045099999999999994, 0.1958, 0.6942])", f1.product(f2).toString());
        assertEquals("Factor({1, 2}, [0.0649, 0.045099999999999994, 0.1958, 0.6942])", f2.product(f1).toString());

        assertEquals("Factor({1, 2, 3}):\n" +
                "{1=0, 2=0, 3=0}: 0.0429\n" +
                "{1=0, 2=0, 3=1}: 0.0671\n" +
                "{1=0, 2=1, 3=0}: 0.0066\n" +
                "{1=0, 2=1, 3=1}: 0.1034\n" +
                "{1=1, 2=0, 3=0}: 0.3471\n" +
                "{1=1, 2=0, 3=1}: 0.5429\n" +
                "{1=1, 2=1, 3=0}: 0.0534\n" +
                "{1=1, 2=1, 3=1}: 0.8366\n",
                product(f1, f3).toStringAsTable("%.4f"));

        assertEquals("Factor({1, 2, 3}):\n" +
                "{1=0, 2=0, 3=0}: 0.0429\n" +
                "{1=0, 2=0, 3=1}: 0.0671\n" +
                "{1=0, 2=1, 3=0}: 0.0066\n" +
                "{1=0, 2=1, 3=1}: 0.1034\n" +
                "{1=1, 2=0, 3=0}: 0.3471\n" +
                "{1=1, 2=0, 3=1}: 0.5429\n" +
                "{1=1, 2=1, 3=0}: 0.0534\n" +
                "{1=1, 2=1, 3=1}: 0.8366\n",
                product(f3, f1).toStringAsTable("%.4f"));

        assertEquals("Factor({1, 2, 3}):\n" +
                "{1=0, 2=0, 3=0}: 0.2301\n" +
                "{1=0, 2=0, 3=1}: 0.3599\n" +
                "{1=0, 2=1, 3=0}: 0.0246\n" +
                "{1=0, 2=1, 3=1}: 0.3854\n" +
                "{1=1, 2=0, 3=0}: 0.0858\n" +
                "{1=1, 2=0, 3=1}: 0.1342\n" +
                "{1=1, 2=1, 3=0}: 0.0468\n" +
                "{1=1, 2=1, 3=1}: 0.7332\n",
                product(f2, f3).toStringAsTable("%.4f"));
        assertEquals("Factor({1, 2, 3}):\n" +
                "{1=0, 2=0, 3=0}: 0.2301\n" +
                "{1=0, 2=0, 3=1}: 0.3599\n" +
                "{1=0, 2=1, 3=0}: 0.0246\n" +
                "{1=0, 2=1, 3=1}: 0.3854\n" +
                "{1=1, 2=0, 3=0}: 0.0858\n" +
                "{1=1, 2=0, 3=1}: 0.1342\n" +
                "{1=1, 2=1, 3=0}: 0.0468\n" +
                "{1=1, 2=1, 3=1}: 0.7332\n",
                product(f3, f2).toStringAsTable("%.4f"));


        assertEquals("Factor({1, 2, 3}):\n" +
                "{1=0, 2=0, 3=0}: 0.0253\n" +
                "{1=0, 2=0, 3=1}: 0.0396\n" +
                "{1=0, 2=1, 3=0}: 0.0027\n" +
                "{1=0, 2=1, 3=1}: 0.0424\n" +
                "{1=1, 2=0, 3=0}: 0.0764\n" +
                "{1=1, 2=0, 3=1}: 0.1194\n" +
                "{1=1, 2=1, 3=0}: 0.0417\n" +
                "{1=1, 2=1, 3=1}: 0.6525\n",
                product(f1, f2, f3).toStringAsTable("%.4f"));

        assertEquals("Factor({1, 2, 3}):\n" +
                "{1=0, 2=0, 3=0}: 0.0253\n" +
                "{1=0, 2=0, 3=1}: 0.0396\n" +
                "{1=0, 2=1, 3=0}: 0.0027\n" +
                "{1=0, 2=1, 3=1}: 0.0424\n" +
                "{1=1, 2=0, 3=0}: 0.0764\n" +
                "{1=1, 2=0, 3=1}: 0.1194\n" +
                "{1=1, 2=1, 3=0}: 0.0417\n" +
                "{1=1, 2=1, 3=1}: 0.6525\n",
                product(f1, f3, f2).toStringAsTable("%.4f"));
        assertEquals("Factor({1, 2, 3}):\n" +
                "{1=0, 2=0, 3=0}: 0.0253\n" +
                "{1=0, 2=0, 3=1}: 0.0396\n" +
                "{1=0, 2=1, 3=0}: 0.0027\n" +
                "{1=0, 2=1, 3=1}: 0.0424\n" +
                "{1=1, 2=0, 3=0}: 0.0764\n" +
                "{1=1, 2=0, 3=1}: 0.1194\n" +
                "{1=1, 2=1, 3=0}: 0.0417\n" +
                "{1=1, 2=1, 3=1}: 0.6525\n",
                product(f3, f1, f2).toStringAsTable("%.4f"));
    }

    @Test
    public void testSum() throws Exception {
        Factor f1 = newFactor(vars(var1), new double[]{0.11, 0.89});
        assertEquals(1.0, f1.sum(), 1e-6);
    }

    @Test
    public void testNormalize() throws Exception {
        Factor f1 = newFactor(vars(var1), new double[]{0.11 * 2, 0.89 * 2});
        f1 = f1.normalize();
        assertEquals("Factor({1}, [0.11, 0.89])", f1.toString());
    }

    @Test
    public void testMarginalize() throws Exception {
        Factor f2 = newFactor(vars(var1, var2), new double[]{0.59, 0.41, 0.22, 0.78});
        Factor f = f2.marginalize(newVarSet(var2));
        assertEquals("Factor({1}, [1.0, 1.0])", f.toString());
    }

    @Test
    public void testObserveEvidence() throws Exception {
        Factor f1 = newFactor(vars(var1), new double[]{0.11, 0.89});

        assertEquals("Factor({1}):\n" +
                "{1=0}: 0.11\n" +
                "{1=1}: 0.89\n", f1.observeEvidence(vars(), new int[]{}).toStringAsTable("%.2f"));
        assertEquals("Factor({1}):\n" +
                "{1=0}: 0.11\n" +
                "{1=1}: 0.00\n", f1.observeEvidence(vars(var1), new int[]{0}).toStringAsTable("%.2f"));
        assertEquals("Factor({1}):\n" +
                "{1=0}: 0.00\n" +
                "{1=1}: 0.89\n", f1.observeEvidence(vars(var1), new int[]{1}).toStringAsTable("%.2f"));
        assertEquals("Factor({1}):\n" +
                "{1=0}: 0.11\n" +
                "{1=1}: 0.89\n", f1.observeEvidence(vars(var2), new int[]{1}).toStringAsTable("%.2f"));

        Factor f2 = newFactor(vars(var1, var2), new double[]{0.59, 0.41, 0.22, 0.78});
        assertEquals("Factor({1, 2}):\n" +
                "{1=0, 2=0}: 0.59\n" +
                "{1=0, 2=1}: 0.00\n" +
                "{1=1, 2=0}: 0.22\n" +
                "{1=1, 2=1}: 0.00\n", f2.observeEvidence(vars(var2), new int[]{0}).toStringAsTable("%.2f"));
        assertEquals("Factor({1, 2}):\n" +
                "{1=0, 2=0}: 0.00\n" +
                "{1=0, 2=1}: 0.41\n" +
                "{1=1, 2=0}: 0.00\n" +
                "{1=1, 2=1}: 0.78\n", f2.observeEvidence(vars(var2), new int[]{1}).toStringAsTable("%.2f"));
        assertEquals("Factor({1, 2}):\n" +
                "{1=0, 2=0}: 0.59\n" +
                "{1=0, 2=1}: 0.41\n" +
                "{1=1, 2=0}: 0.00\n" +
                "{1=1, 2=1}: 0.00\n", f2.observeEvidence(vars(var1), new int[]{0}).toStringAsTable("%.2f"));
        assertEquals("Factor({1, 2}):\n" +
                "{1=0, 2=0}: 0.00\n" +
                "{1=0, 2=1}: 0.00\n" +
                "{1=1, 2=0}: 0.22\n" +
                "{1=1, 2=1}: 0.78\n", f2.observeEvidence(vars(var1), new int[]{1}).toStringAsTable("%.2f"));
        assertEquals("Factor({1, 2}):\n" +
                "{1=0, 2=0}: 0.00\n" +
                "{1=0, 2=1}: 0.00\n" +
                "{1=1, 2=0}: 0.22\n" +
                "{1=1, 2=1}: 0.00\n", f2.observeEvidence(vars(var1, var2), new int[]{1, 0}).toStringAsTable("%.2f"));
    }

    @Test
    public void testInducedMarkovNetwork() throws Exception {
        Factor[] factors = StudentsNetwork.buildStudentsNetwork().getFactors();
        DataGraph<Var, List<Factor>> network = Factor.induceMarkovNetwork(factors);

        assertEquals("DataGraphImpl{isDirected=false, [\n" +
                "    D<->G:[Factor({G, I, D}, [0.3, 0.05, 0.9, 0.5, 0.4, 0.25, 0.08, 0.3, 0.3, 0.7, 0.02, 0.2])]\n" +
                "    D<->I:[Factor({G, I, D}, [0.3, 0.05, 0.9, 0.5, 0.4, 0.25, 0.08, 0.3, 0.3, 0.7, 0.02, 0.2])]\n" +
                "    G<->I:[Factor({G, I, D}, [0.3, 0.05, 0.9, 0.5, 0.4, 0.25, 0.08, 0.3, 0.3, 0.7, 0.02, 0.2])]\n" +
                "    G<->L:[Factor({L, G}, [0.1, 0.4, 0.99, 0.9, 0.6, 0.01])]\n" +
                "    I<->S:[Factor({S, I}, [0.95, 0.2, 0.05, 0.8])]\n" +
                "]}",
                network.toString());

    }
}
