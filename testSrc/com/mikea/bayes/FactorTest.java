package com.mikea.bayes;

import org.junit.Test;

import static com.mikea.bayes.Factor.newFactor;
import static com.mikea.bayes.Factor.product;
import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class FactorTest {
    private static final ProbabilitySpace space = new ProbabilitySpace(4, new int[] {2, 2, 2, 2});
    @Test
    public void testToString() throws Exception {
        Factor f1 = newFactor(space, new int[]{1}, new double[]{0.11, 0.89});
        Factor f2 = newFactor(space, new int[]{2, 1}, new double[]{0.59, 0.41, 0.22, 0.78});
        assertEquals("Factor({1}, [0.11, 0.89])", f1.toString());
        assertEquals("Factor({1, 2}, [0.59, 0.22, 0.41, 0.78])", f2.toString());
    }

    @Test
    public void testProduct() throws Exception {
        Factor f1 = newFactor(space, new int[]{1}, new double[]{0.11, 0.89});
        Factor f2 = newFactor(space, new int[]{2, 1}, new double[]{0.59, 0.41, 0.22, 0.78});
        Factor f3 = newFactor(space, new int[]{3, 2}, new double[]{0.39, 0.61, 0.06, 0.94});

        assertEquals("Factor({1, 2}, [0.0649, 0.1958, 0.045099999999999994, 0.6942])", f1.product(f2).toString());
        assertEquals("Factor({1, 2}, [0.0649, 0.1958, 0.045099999999999994, 0.6942])", f2.product(f1).toString());

        assertEquals("Factor({1, 2, 3}, [0.0429, 0.3471, 0.0066, 0.053399999999999996, 0.06709999999999999, 0.5429, 0.10339999999999999, 0.8366])",
                product(f1, f3).toString());
        assertEquals("Factor({1, 2, 3}, [0.0429, 0.3471, 0.0066, 0.053399999999999996, 0.06709999999999999, 0.5429, 0.10339999999999999, 0.8366])",
                product(f3, f1).toString());

        assertEquals("Factor({1, 2, 3}, [0.2301, 0.0858, 0.024599999999999997, 0.0468, 0.3599, 0.13419999999999999, 0.38539999999999996, 0.7332])",
                product(f2, f3).toString());
        assertEquals("Factor({1, 2, 3}, [0.2301, 0.0858, 0.024599999999999997, 0.0468, 0.3599, 0.13419999999999999, 0.38539999999999996, 0.7332])",
                product(f3, f2).toString());


        assertEquals("Factor({1, 2, 3}, [0.025311, 0.076362, 0.0027059999999999996, 0.041652, 0.039589, 0.119438, 0.042393999999999994, 0.652548])",
                product(f1, f2, f3).toString());
        assertEquals("Factor({1, 2, 3}, [0.025311, 0.076362, 0.0027059999999999996, 0.041652, 0.03958899999999999, 0.11943800000000002, 0.042393999999999994, 0.652548])",
                product(f1, f3, f2).toString());
        assertEquals("Factor({1, 2, 3}, [0.025311, 0.076362, 0.0027059999999999996, 0.041652, 0.03958899999999999, 0.11943800000000002, 0.042393999999999994, 0.652548])",
                product(f3, f1, f2).toString());
    }

    @Test
    public void testSum() throws Exception {
        Factor f1 = newFactor(space, new int[]{1}, new double[]{0.11, 0.89});
        assertEquals(1.0, f1.sum(), 1e-6);
    }

    @Test
    public void testNormalize() throws Exception {
        Factor f1 = newFactor(space, new int[]{1}, new double[]{0.11 * 2, 0.89 * 2});
        f1 = f1.normalize();
        assertEquals("Factor({1}, [0.11, 0.89])", f1.toString());
    }

    @Test
    public void testMarginalize() throws Exception {
        Factor f2 = newFactor(space, new int[]{2, 1}, new double[]{0.59, 0.41, 0.22, 0.78});
        Factor f = f2.marginalize(newVarSet(space, new int[]{2}));
        assertEquals("Factor({1}, [1.0, 1.0])", f.toString());
    }

    @Test
    public void testObserveEvidence() throws Exception {
        Factor f1 = newFactor(space, new int[]{1}, new double[]{0.11, 0.89});

        assertEquals("Factor({1}, [0.11, 0.89])", f1.observeEvidence(new int[]{}, new int[] {}).toString());
        assertEquals("Factor({1}, [0.11, 0.0])", f1.observeEvidence(new int[]{1}, new int[] {0}).toString());
        assertEquals("Factor({1}, [0.0, 0.89])", f1.observeEvidence(new int[]{1}, new int[] {1}).toString());
        assertEquals("Factor({1}, [0.11, 0.89])", f1.observeEvidence(new int[]{2}, new int[] {1}).toString());

        Factor f2 = newFactor(space, new int[]{2, 1}, new double[]{0.59, 0.41, 0.22, 0.78});
        assertEquals("Factor({1, 2}, [0.59, 0.22, 0.0, 0.0])", f2.observeEvidence(new int[]{2}, new int[] {0}).toString());
        assertEquals("Factor({1, 2}, [0.0, 0.0, 0.41, 0.78])", f2.observeEvidence(new int[]{2}, new int[] {1}).toString());
        assertEquals("Factor({1, 2}, [0.59, 0.0, 0.41, 0.0])", f2.observeEvidence(new int[]{1}, new int[] {0}).toString());
        assertEquals("Factor({1, 2}, [0.0, 0.22, 0.0, 0.78])", f2.observeEvidence(new int[]{1}, new int[] {1}).toString());
        assertEquals("Factor({1, 2}, [0.0, 0.22, 0.0, 0.0])", f2.observeEvidence(new int[]{1, 2}, new int[] {1, 0}).toString());
    }
}
